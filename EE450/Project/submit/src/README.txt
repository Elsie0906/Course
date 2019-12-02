Yin-Hsia Yen

USC ID: 4337-8177-05

a. What I have done in this project?
	1. Studied chapter 5 and 6 in "Beej's Guide to Network Programming"
	2. Implemented 4 components in this assignment, including client, aws server, serverA and serverB.
	   The client connects with aws server using TCP protocal.
	   The serverA and serverB connect with aws server using UDP protocal.
	3. Implemented Dijkstra's algorithm to find out shortest path from a given start node(referrence: Lecture "Routing Protocals")

b. What would be included in my files?
	1. client.cpp
	   - Create a TCP client socket
	   - Connect between client and aws server
	   - Serialize the input argument and send to the aws server
	   - Receive the response from the aws server
	   - Close the TCP client socket
	2. aws.h
	   - Define a data structure including needed info that would send back to the client
	   aws.cpp
	   - Create a TCP server socket and listen on client
	   - Receive the input from the client (mapID, start node, file size)
	   - Create a UDP socket and configure the socket for the connection aws-serverA and aws-serverB
	   - Send the data (mapID and start node)to serverA and receive the output from it
	   - Send the data to serverB and receive the output from it
	   - Send the result to the client (through TCP child socket)
	3. serverA.h
	   - Define data structures that would be needed in step "Map Construction" and "Path Finding"
	   serverA.cpp
	   - Create a UDP socket
	   - Read from the file "map.txt" and construct a list of graphs
	   - Receive info (mapID and start node) from aws server
	   - Use Dijkstra algorithm to find the path of min length from a given start vertex to all other nodes
	   - Send the result (propagation speed, bit rate, min length array) to the aws server
	4. serverB.cpp
	   - Create a UDP socket
	   - Receive info (propagation speed, bit rate, file size, min length array) from aws server
	   - Calculate the end to end delay with a given file size in the selected graph
	   - Send the result (end-to-end delay) to the aws server

c. The format of all the messages exchanged
	1. client.cpp
	   - The client is up and running
	   - The client has sent query to AWS using TCP: start vertex <#>;map <#>; file size <#>
	   - The client has received results from AWS:
	     <Destionation> <Min Length> <Tt> <Tp> <Delay>
	2. aws.cpp
	   - The AWS is up and running
	   - The AWS has received map ID <#>, start vertex <#> and file size <#> from the client using TCP over port <#>
	   - The AWS has sent map ID and starting vertex to server A using UDP over port <#>
	   - The AWS has received shortest path from server A:
	     <Destionation> <Min Length>
	   - The AWS has sent path length, propagation speed and transmission speed to server B using UDP over port <#>
	   - The AWS has received delays from server B:
	     <Destionation> <Tt> <Tp> <Delay>
	   - The AWS has sent calculated delay to client using TCP over port <#>
	3. serverA.cpp
	   - The Server A is up and running using UDP on port <#>
	   - The Server A has constructed a list of <#> maps:
	     <Map ID> <Num Vertices> <Num Edges>
	   - The Server A has received input for finding shortest paths: starting vertex <#> of map <#>
	   - The Server A has identified the following shortest paths:
	     <Destionation> <Min Length>
	   - The Server A has sent shortest paths to AWS
	4. serverB.cpp
	   - The Server B is up and running using UDP on port <#>
	   - The Server B has received data for calculation:
	     * Propagation speed: <#> km/s;
	     * Transmission speed <#> Bytes/s;
	     * Path length for destination <#>:<#>;
	   - The Server B has finished the calculation of the delays:
	     <Destionation> <Tt> <Tp> <Delay>
	   - The Server B has finished sending the output to AWS

d. Any idiosyncrasy of your project
   - print err msg when input argument is not in the correct format
   - print err msg when file "map.txt" does not exist

e. Reused Code
   - Reference code from the Beej's programming guide

