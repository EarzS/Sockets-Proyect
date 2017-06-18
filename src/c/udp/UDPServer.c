/* A simple server in the internet domain using UDP
   http://www.binarytides.com/programming-udp-sockets-c-linux/ */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>

#define BUFFER_SIZE 8000
#define PORT 7878

void error(const char *msg)
{
    perror(msg);
    exit(1);
}

int main(int argc, char *argv[]) {
    // Connection address
    int port = PORT;

    // Buffer of messages
    char buffer[BUFFER_SIZE];

    // Socket data
    int sockfd;
    struct sockaddr_in serv_addr, cli_addr;
    int recv_len;
    int slen = sizeof(cli_addr);
    
    // Creating socket
    sockfd = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
    if (sockfd < 0) {
        error("ERROR opening socket");
    }
    
    // Binding the server to a ip address
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(port);
    if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
        error("ERROR on binding");
    }
    
    while(1) {
        // Reading messages
        recv_len = recvfrom(sockfd, buffer, BUFFER_SIZE, 0, (struct sockaddr *) &cli_addr, &slen);
        if(recv_len < 0) {
            error("ERROR on receiving");
        }
        printf("Here is the message: %s\n", buffer);
        
        // Responding message
        if(sendto(sockfd, buffer, recv_len, 0, (struct sockaddr*) &cli_addr, slen) < 0) {
            error("ERROR on sending");
        }
    }
    
    // Closing the connection 
    close(sockfd);

    return 0;
}