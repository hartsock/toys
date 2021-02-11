#!/usr/bin/env bash

# presumes a lot about your env.
# presumes you've got find installed
# presumes you've got bash
# presumes you've built the project
# presumes java is on the path.

java -jar `find . -name "toys-cli*with-dependencies.jar"` "$@"

# if this wasn't a toy (and I were taking things seriously) I would spend time to fix this.