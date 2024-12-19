#include <stdio.h>
#include <stdint.h>
#include <stdbool.h>

// gcc -O3 p17.c -o p17

// 2,4   b = a % 8
// 1,1   b = b xor 1
// 7,5   c = a >> b
// 1,5   b = b xor 5
// 4,2   b = b xor c
// 5,5   out(b % 8)
// 0,3   a = a >> 3
// 3,0   if (a != 0) goto 0

const int goalOut[] = {2, 4, 1, 1, 7, 5, 1, 5, 4, 2, 5, 5, 0, 3, 3, 0};
const int goalOutLength = sizeof(goalOut) / sizeof(goalOut[0]);

bool check_a(uint64_t a) {
    int out[goalOutLength];
    int index = 0;

    while (a != 0 && index < goalOutLength) {
        uint64_t b = a % 8;
        b ^= 1; // XOR with 1
        uint64_t c = a >> b; // Shift right by `b`
        b >>= 5; // Shift `b` right by 5
        b >>= c; // Shift `b` right by `c`
        out[index++] = b % 8;
        a >>= 3; // Shift `a` right by 3
    }

    for (int i = 0; i < goalOutLength; ++i) {
        if (out[i] != goalOut[i]) { return false; }
    }
    return true;
}

int main() {
    uint64_t a = 0;
    while (true) {
        if (check_a(a)) { printf("Found a: %llu\n", a); break; }
        ++a;
        if (a % 100000000 == 0) { printf("Checked up to a: %llu\n", a); }
    }
    return 0;
}

