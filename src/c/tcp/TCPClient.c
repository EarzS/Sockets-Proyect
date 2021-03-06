/* A simple client in the internet domain using TCP
   http://www.linuxhowtos.org/C_C++/socket.htm
   http://es.tldp.org/Tutoriales/PROG-SOCKETS/prog-sockets.html */
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
    struct hostent *server;

    // Creating socket
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) {
        error("ERROR opening socket");
    }

    // Getting the server host address
    server = gethostbyname(hostname);
    if (server == NULL) {
        fprintf(stderr,"ERROR, no such host\n");
        exit(0);
    }

    // Connecting to server
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    bcopy((char *)server->h_addr, 
         (char *)&serv_addr.sin_addr.s_addr,
         server->h_length);
    serv_addr.sin_port = htons(port);
    if (connect(sockfd,(struct sockaddr *) &serv_addr,sizeof(serv_addr)) < 0) {
        error("ERROR connecting");
    }

    // Receiving input
    printf("Please enter the message: ");
    bzero(buffer,BUFFER_SIZE);
    fgets(buffer,BUFFER_SIZE,stdin);

    // Sending input
    n = write(sockfd,buffer,strlen(buffer));
    if (n < 0) {
        error("ERROR writing to socket");        
    }
        
    // Receiving data from server
    bzero(buffer,BUFFER_SIZE);
    n = read(sockfd,buffer,BUFFER_SIZE);
    if (n < 0) {
        error("ERROR reading from socket");
    }
    printf("%s\n",buffer);

    // Closing the socket
    close(sockfd);

    return 0;
}

/*
int main(int argc, char *argv[])
{
    int sockfd, portno, n;
    struct sockaddr_in serv_addr;
    struct hostent *server;

    char buffer[256];
    if (argc < 3) {
       fprintf(stderr,"usage %s hostname port\n", argv[0]);
       exit(0);
    }
    portno = atoi(argv[2]);
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) 
        error("ERROR opening socket");
    server = gethostbyname(argv[1]);
    if (server == NULL) {
        fprintf(stderr,"ERROR, no such host\n");
        exit(0);
    }
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    bcopy((char *)server->h_addr, 
         (char *)&serv_addr.sin_addr.s_addr,
         server->h_length);
    serv_addr.sin_port = htons(portno);
    if (connect(sockfd,(struct sockaddr *) &serv_addr,sizeof(serv_addr)) < 0) 
        error("ERROR connecting");
    printf("Please enter the message: ");
    bzero(buffer,256);
    fgets(buffer,255,stdin);
    n = write(sockfd,buffer,strlen(buffer));
    if (n < 0) 
         error("ERROR writing to socket");
    bzero(buffer,256);
    n = read(sockfd,buffer,255);
    if (n < 0) 
         error("ERROR reading from socket");
    printf("%s\n",buffer);
    close(sockfd);
    return 0;
}*/
