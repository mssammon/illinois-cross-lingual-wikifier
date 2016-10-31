#!/bin/sh

###
# Run a benchmark test class that evaluates cross-lingual wikifier models on 
# a small subset of the TAC 2016 EDL data
#
# Running example:
# scripts/run-benchmark.sh es config/xlwikifier-tac.config
#
# Two options for LANG: "es" and "zh"
#
# Using configuration file config/xlwikifier-tac.config, you will get the following performance:
#
# Spanish (es):
# Mention Span: Precision:0.9212 Recall:0.8302 F1:0.8734
# Mention Span + Entity Type: Precision:0.8836 Recall:0.7963 F1:0.8377
# Mention Span + Entity Type + FreeBase ID: Precision:0.7740 Recall:0.6975 F1:0.7338
#
# Chinese (zh):
# Mention Span: Precision:0.8994 Recall:0.6859 F1:0.7782
# Mention Span + Entity Type: Precision:0.8742 Recall:0.6667 F1:0.7565
# Mention Span + Entity Type + FreeBase ID: Precision:0.7610 Recall:0.5803 F1:0.6585

LANG=$1
CONFIG=$2

CP="./target/dependency/*:./target/classes/"
java -ea -Xmx30g -cp $CP edu.illinois.cs.cogcomp.xlwikifier.evaluation.TAC2016Eval $LANG $CONFIG

