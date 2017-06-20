/* A simple server in the internet domain using UDP Multicast
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>

#define BUFFER_SIZE 6789
#define PORT 7878
#define GROUP_ADDRESS "228.5.6.7"

#define SENDER

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
    struct sockaddr_in serv_addr;
    int recv_len;
    int slen;
    
    // Creating socket
    sockfd = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
    if (sockfd < 0) {
        error("ERROR opening socket");
    }
    
    // Set destination address
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(port);
    
#ifdef SENDER
    serv_addr.sin_addr.s_addr = inet_addr(GROUP_ADDRESS);
    
    while(1) {
        // Receiving input
        printf("Please enter the message: ");
        bzero(buffer,BUFFER_SIZE);
        fgets(buffer,BUFFER_SIZE,stdin);
        
        // Responding message
        if(sendto(sockfd, buffer, recv_len, 0, (struct sockaddr*) &serv_addr, slen) < 0) {
            error("ERROR on sending");
        }
    }
#else
    struct ip_mreq mreq;
    
    // Bind to receive connection
    if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
        error("ERROR on binding");
    }
    // Allowing receiving messages
    mreq.imr_multiaddr.s_addr = inet_addr(GROUP_ADDRESS);         
    mreq.imr_interface.s_addr = htonl(INADDR_ANY);  
    if (setsockopt(sockfd, IPPROTO_IP, IP_ADD_MEMBERSHIP, &mreq, sizeof(mreq)) < 0) {
        error("ERROR on setsockopt mreq");
    }   
    
    while(1) {
        // Reading messages
        recv_len = recvfrom(sockfd, buffer, BUFFER_SIZE, 0, (struct sockaddr *) &serv_addr, &slen);
        if(recv_len < 0) {
            error("ERROR on receiving");
        }
        printf("Here is the message: %s\n", buffer);
    }
#endif
    
    // Closing the connection 
    close(sockfd);

    return 0;
}