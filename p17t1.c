#include <stdio.h>
#include <stdint.h>
#include <stdbool.h>

// gcc -O3 p17t1.c -o p17t1

// 0,3 a = a >> 3
// 5,4 out(a % 8)
// 3,0 if (a != 0) 0

const int goalOut[] = {0,3,5,4,3,0};
const int goalOutLength = sizeof(goalOut) / sizeof(goalOut[0]);

void printArr(int *arr) {
    for (int i = 0; i < goalOutLength; ++i) {
        printf("%d,", arr[i]);
    }
    printf("\n");
}

bool check_a(uint64_t a) {
    printf("a = %llu\n", a);

    int out[goalOutLength];
    int index = 0;

    while (a != 0 && index < goalOutLength) {
        a >>= 3;  // shift A right by 3
        out[index++] = a % 8; // out(a % 8)
    }

    for (int i = 0; i < goalOutLength; ++i) {
        if (out[i] != goalOut[i]) {
            printArr((int*)out);
            return false;
        }
    }

    printArr((int*)out);
    return true;
}

int main() {
    //printArr((int*)goalOut);
    uint64_t a = 0;
    while (true) {

    //uint64_t a = 2024;
    //while (a < 2030) {
        if (check_a(a)) {
            printf("Found a: %llu\n", a);
            break;
        }
        ++a;
        if (a % 100000000 == 0) { printf("Checked up to a: %llu\n", a); }
    }
    return 0;
}

