# Negotiate fails with a load-balancer
----

## Question
I've been spending quite a bit of time trying to get my configuration to work and have had no successful. I've found some posts on this discussion site that are sort of related but I've not been able to piece it together. My configuration and problem is explained below.

On a purely windows domain I have two tomcat servers (server1, server2) that have a test jsp page. It has been set up with waffle to retrieve the windows username who is accessing the JSP. From a client (client1), also on the domain, I can use IE to view the JSP directly from either server, e.g. http://server1:8080/cluster/test.jsp . The JSP includes displaying the windows user I am logged into the client as and which server processed the request, e.g. DOMAIN\user1 server1. This seems to prove that waffle is configured OK.

I would like to then use Apache HTTP Server v2.2 with mod_proxy_balancer as a load balancer. It is located on a different server, e.g. serverlb, but is on the domain. I have a basic configuration of mod_proxy_balancer that seems to work with server1 and server2 when waffle isn't used, i.e. the load balancer correctly passes the request to one of the two tomcat servers. It isn't showing the windows username but I assume that is OK as waffle isn't used at this point. This seems to prove that the load balancer is configured OK.

Then I try to put waffle and the load banacer together. I try to access the test JSP from the load balancer while waffle is used, e.g. http://serverlb:8080/cluster/test.jsp using the same user (DOMAIN\User1) and same client (client1). This time it shows a dialog box for a username and password. If I enter the credentials for the DOMAIN\user1 (who I am logged in as on the client), it fails to authenicate. It seems the only credentials that work are for a local user found on the load balancer server, e.g. SERVERLB\Administrator. These credentials are accepted and the jsp is displayed. The JSP shows which one of the two servers processed the JSP (e.g. server1 or server2) but the interesting thing is the user is reported as SERVER1\Administrator or SERVER2\Administrator, i.e. it has translated the local administrator I provided as credentials (SERVERLB\Administrator) to equivalent local administor of the tomcat server.

I don't know what is needed to fix it. I've tried many things and have ran out of ideas. I'm not sure if it's a tomcat, waffle or appache http server problem / configuration. It seems the credentials entered into the dialog box are checked against the local users on serverlb.

The desired effect is the tomcat servers at the back end get the windows username of the user accessing the page without any login dialog box. The load balancer distributes the request to both the tomcat servers.

Any help, comments, tips etc would be greatly appreciated,

David

# Answer
> I am going to speculate that if the url between the load balancer and the server behind it changes, this can't always work. But what I would do is isolate the request/response that fails and go through the normal troubleshooting steps.

I've taken up davram's work on this issue, apologies for the delayed reply. Having run a network capture I've found that the problem basically boils down to the fact that the backend server (eg. server1) receives a  "service ticket" that was intended for serverlb. Obviously server1 is unable to successfully decrypt the ticket to verify the user (seeing as it was intended for serverlb) so responds with a KRB_AP_ERR_MODIFIED. At this point NTLM kicks in and a username/password dialogue is displayed to the user.

To solve this issue I created a virtual host name (DNS entry) for the proxy server and created a service account that the tomcat services on server1 and server2 will run as. On the domain controller an SPN for the proxy's virtual host name is created and mapped to the service account. When the user uses the virtual host name to browse to the proxy server they are automatically authenticated to the backend server via the kerberos protocol.

These steps in more detail are...

1. Create a DNS entry for serverlb (eg. "cluster"). The DNS entry must be of type 'A' (not CNAME) and must map to the IP of the proxy server
1. Create a domain account that the tomcat services will run as (eg. "tcservice")
1. On the tomcat servers update the tomcat service to run as the "tcservice" account (not Local System)
1. On the tomcat servers change the permissions on the tomcat program root directory to allow "full access" to the "tcservice" account
1. On the domain controller add an SPN for the virtual host name mapping it to the "tcservice" account. To do this enter the following command into a command terminal

setspn -a HOST/cluster.<domain> <domain>\tcservice

Now when you use the virtual host name to access the test JSP (eg. http://cluster:8080/cluster/test.jsp) no username/password dialogue is presented, you are automatically authenticated to the backend server and the username DOMAIN\user1 is displayed on the webpage.
