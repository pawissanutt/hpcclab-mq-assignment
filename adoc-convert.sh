#!/usr/bin/env bash

# apt install pandoc asciidoctor
asciidoctor -b docbook README.adoc
pandoc -f docbook -t gfm README.xml -o README.md
