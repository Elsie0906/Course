1. stdin

Technically, stdin simply means "file descriptor 0". Usually, you launch a program from your Unix login shell. Then it's the job of the login shell to determine what stdin is mapped to. By default, your login shell maps stdin to keyboard input. But this behavior can be changed, and this is called I/O redirection. Let's say that your program is prog and you want the content of /tmp/xyz.txt to appear as your stdin, you can do:
    prog < /tmp/xyz.txt
This is called I/O redirection and it's a trick performed by your login shell. Another way to do the same thing is to use the cat program and the Unix pipe:
    cat /tmp/xyz.txt | prog

2. file desciptor/ (FILE*)

A file descriptor is an integers. It's an index for a file descriptor table that the kernel maintains for the corresponding user process. To use a file descriptor, you need to make system calls (such as open(), read(), write(), and close()) and these system calls are not too "application programmer friendly".
A file pointer, which is of the type (FILE*), is a "wrapper" around a file descriptor. Lots of functions are built around file pointers to make it easier to deal with files.

When a user process is started, file descriptors 0, 1, and 2 are opened by default. File descriptor 0 is associated with the keyboard, and file descriptors 1 and 2 are associated with the display. The corresponding file pointers are also setup by default. The file pointers that's associated with file descriptor 0, 1, 2 are known as stdin, stdout, and stderr, respectively.

