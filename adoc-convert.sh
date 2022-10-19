#!/usr/bin/env bash

# apt install pandoc asciidoctor
asciidoctor -b docbook _README.adoc
pandoc -f docbook -t gfm _README.xml -o README.md

# Unicode symbols were mangled in foo.md. Quick workaround:
# iconv -t utf-8 _README.xml | pandoc -f docbook -t gfm | iconv -f utf-8 > README.md
