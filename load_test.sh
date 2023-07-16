#!/bin/bash

echo 'Starting load test environment'

PRO_NAME=load-test

tmux new-session -d -s $PRO_NAME
tmux send-keys './linux_dev.sh' 'C-l' 'C-m'

tmux split-window -v
tmux send-keys 'cd src/test/python && source venv/bin/activate && ulimit -n 1048576 && clear' 'C-l' 'C-m'

tmux attach-session -t $PRO_NAME

