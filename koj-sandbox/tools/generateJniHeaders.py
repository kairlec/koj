import os
import subprocess
import sys


def get_arg(arg_name, default=None):
    for arg in sys.argv:
        if arg.startswith(arg_name):
            return arg.split('=')[1]
    return default


def get_arg_required(arg_name, default=None):
    arg = get_arg(arg_name, default)
    if arg is None:
        raise Exception('missing argument: ' + arg_name)
    return arg


def get_file_tree(path, endswith=None):
    files = []
    for root, _, filenames in os.walk(path):
        for filename in filenames:
            if endswith is None or filename.endswith(endswith):
                files.append(os.path.join(root, filename))
    return files


def run_javac(javac, output_dir, src_files, classpath):
    args = [javac, '-h', output_dir, '-cp', classpath]
    for src_file in src_files:
        args.append(src_file)
    print('command: ' + ' '.join(args))
    return subprocess.call(args)


def generate():
    javac = get_arg_required('--javac')
    src_dir = get_arg_required('--src-dir', 'src/main/java')
    output_dir = get_arg_required('--output-dir', 'src/main/generated/jni')
    package_name = get_arg('--package-name')
    classpath = get_arg('--classpath')
    if package_name is not None:
        src_dir = os.path.join(src_dir, package_name.replace('.', '/'))
    src_files = get_file_tree(src_dir, endswith='.java')
    return run_javac(javac, output_dir, src_files, classpath)


if __name__ == '__main__':
    exit(generate())
