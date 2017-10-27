#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_4940b5ffd765_key -iv $encrypted_4940b5ffd765_iv -in .travis/signingkey.asc.enc -out .travis/signingkey.asc -d
    gpg --fast-import .travis/signingkey.asc
fi