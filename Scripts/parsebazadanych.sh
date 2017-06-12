#!/bin/bash
# Written by Kamil Cukrowski 2017
#
#echo "baza danych pobrana  z https://zasadyzywienia.pl/tabele-wartosci-odzywczych-czesc-iii.html"
#echo "Parsowanie"

download() {
	pushd /tmp/
	wget --no-check-certificate https://zasadyzywienia.pl/tabele-wartosci-odzywczych-czesc-i.html
	wget --no-check-certificate https://zasadyzywienia.pl/tabele-wartosci-odzywczych-czesc-ii.html
	wget --no-check-certificate https://zasadyzywienia.pl/tabele-wartosci-odzywczych-czesc-iii.html
	popd
	cat /tmp/tabele-wartosci-odzywczych-czesc-i.html \
	/tmp/tabele-wartosci-odzywczych-czesc-ii.html \
	/tmp/tabele-wartosci-odzywczych-czesc-iii.html | \
	egrep "^<p><strong>|^[[:space:]]*<td class=\"column-1\">[^<]"  > /tmp/bazadanych.txt
}
parse() {
	kategoria="Nieznana"
	while read -r line; do 
		if [[ "$line" =~ ^\<p\>\<strong\> ]]; then
			kategoria=$(echo "$line" | sed 's;.*<strong>\([^<]*\)</strong>.*;\1;')
		fi
		if [[ "$line" =~ ^\<td\ class=\"column-1\"\> ]]; then
			echo -n "$kategoria|"; 
			echo -n "$line" | sed -e 's;</td>;|;g' -e 's/|$//' -e 's;<[^>]*>;;g' -e 's/,/./g' -e 's/b\.d\./-1/g'
			echo
		fi
	done </tmp/bazadanych.txt >/tmp/parsedbazadanych.csv
}
kategorie() {
	cat /tmp/parsedbazadanych.csv | sed 's/^\([^|]*\)|.*/\1/' | sort -u | \
		nl -s'|' | sed -e 's/^[ ]*//' > /tmp/categories.csv
	while read -r line; do 
		echo -n "$(grep "$(echo "$line"|cut -d'|' -f1)" </tmp/categories.csv | cut -d'|' -f1)"
		echo -n "|$(echo "$line"|cut -d'|' -f2-)"
		echo
	done </tmp/parsedbazadanych.csv | nl -s'|' | sed -e 's/^[ ]*//' >/tmp/ingridients.csv
}
sqliteimport() {
	set -x
	sqlite3 /tmp/bazadanych.sqlite3 < <(cat <<"EOF"
.echo on
drop table if exists categories;
create table categories (
 id INTEGER PRIMATY KEY, 
 name TEXT
);
drop table if exists ingridients;
create table ingridients  (
 id INTEGER PRIMARY KEY, 
 category_id INTEGER,
 name TEXT,
 calories REAL,
 protein REAL,
 fat REAL,
 carbs REAL,
 fiber REAL,
 FOREIGN KEY(category_id) REFERENCES ingridients(id)
);
.mode csv
.separator "|"
.import /tmp/ingridients.csv ingridients
.import /tmp/categories.csv  categories
select * from categories limit 10;
select * from ingridients limit 10;
EOF
)
}