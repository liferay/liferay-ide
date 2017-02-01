#!/bin/sh

##
## Utilities used in ospackage scripts. This file should be included once on
## top of every script from 'pkg_scripts' / 'init.d'.
##

die() {
    echo "$1" >&2
    exit 127
}