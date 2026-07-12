#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
int main(int argc, char **argv) {
  if (argc < 3) {
    fprintf(stderr, "usage: run-as-uid UID cmd [args...]\n");
    return 1;
  }
  uid_t uid = (uid_t)atoi(argv[1]);
  if (setgid(uid) != 0) { perror("setgid"); return 1; }
  if (setuid(uid) != 0) { perror("setuid"); return 1; }
  execvp(argv[2], &argv[2]);
  perror("execvp");
  return 127;
}