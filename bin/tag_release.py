#! /usr/bin/env python3

import argparse
import subprocess


def parse_args(argv=None):
    parser = argparse.ArgumentParser('tag-release')
    parser.add_argument('git_tag')
    return parser.parse_args(args=argv)



def local_tag_exists(tag):
    if subprocess.check_output(f'git tag -l | grep {tag}'.split()):
        return True


def remote_tag_exists(tag):
    if subprocess.check_output(f'git ls-remote --tags origin | grep {tag}'.split()):
        return True


def create_local_tag(tag):
    subprocess.call(f'git tag -m "{tag}" {tag}'.split())
    if not local_tag_exists(tag):
        raise Exception(f'Could not create tag {tag}')


def push_tags(tag):
    subprocess.call(f'git push --tags'.split())
    if not remote_tag_exists(tag):
        raise Exception(f'Could not push tags')


def main(argv=None):
    args = parse_args(argv=argv)
    tag = args.git_tag
    if remote_tag_exists(tag):
        print(f'Tag {tag} already exists on remote. Nothing to do.')
        return

    if not local_tag_exists(tag):
        create_local_tag(tag)

    push_tags(tag)


if __name__ == '__main__':
    main()
