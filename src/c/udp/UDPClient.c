/* A simple client in the internet domain using UDP
 http://www.binarytides.com/programming-udp-sockets-c-linux/ */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h> 

#define BUFFER_SIZE 8000
#define PORT 7878
#define HOSTNAME "debian"

void error(const char *msg)
{
    perror(msg);
    exit(1);
}

int main(int argc, char *argv[]) {
    // Connection address
    int port = PORT;
    char hostname[] = HOSTNAME;

    // Buffer of messages
    char buffer[BUFFER_SIZE];

    // Socket data
    int sockfd, n;
    struct sockaddr_in serv_addr;
    int slen = sizeof(serv_addr);
    
    // Creating socket
    sockfd = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
    if (sockfd < 0) {
        error("ERROR opening socket");
    }

    // Getting the server address
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(port);
    if (inet_aton(hostname, &serv_addr.sin_addr) == 0) {
        error("ERROR inet_aton()");
    }
    
    while(1) {
        // Receiving input
        printf("Please enter the message: ");
        bzero(buffer,BUFFER_SIZE);
        fgets(buffer,BUFFER_SIZE,stdin);
        
        // Sending data to server
        n = sendto(socketfd, buffer, BUFFER_SIZE, 0, (struct sockaddr *) &serv_addr, slen);
        if(n < 0) {
            error("ERROR sending to socket");
        }
        
        // Receiving response from server
        bzero(buffer,BUFFER_SIZE);
        n = recvfrom(sockfd, buffer, BUFFER_SIZE, (struct sockaddr *) &serv_addr, &slen);
        if (n < 0) {
            error("ERROR reading from socket");
        }
        printf("%s\n",buffer);
    }

    // Closing the socket
    close(sockfd);

    return 0;
}