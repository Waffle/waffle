# Server returns 401 Access Denied with the AP_ERR_MODIFIED error code:
----

## Question
I'm new to Waffle and Kerberos, and I think I have an SPN problem... can you help me straighten this out, please?

Here's the SPN that I've created, using a User account named KerberosTestApp:

setspn -L MYDOMAIN\KerberosTestApp
Registered ServicePrincipalNames for CN=KerberosTestApp,CN=Users,DC=mydomain,DC=com:
    HTTP/theServer.name.com
    HTTP/theServer

My server's Computer account doesn't have an explicit HOST/theServer SPN... the mapping is done dynamically, isn't it?

At login time, my client is granted a service ticket (TGS-REP) that says the server name (service and instance) is HTTP/theServer.name.com.

The ticket is sent in the next GET, for which the server returns a 401 with the AP_ERR_MODIFIED error code. The server name that appears in the error message is (service and host) HOST/theServer.name.com.

Note the difference in service class. Is that a problem, or is it just that the server chose a different name type? If it is the problem, how can I fix it? Did I create my SPNs backwards?

# Answer
Okay, I got it. For any other newbies out there:

Kerberos is implemented through the exchange of encrypted and signed messages. Some of the encryption keys are based on the passwords of accounts in the Active Directory. The client machine uses the account and password of the user who has logged in. When the client requests a ticket for access to the remote service, an SPN tells the Active Directory which service account's password to use.

But how does the server application know which account and password to use? The AP_ERR_MODIFIED error code means the server can't decrypt something or verify a signature, so clearly the password it's using is wrong.

If you login to your server and run your app by executing a batch file, etc, Kerberos authentication won't work. The application runs under your account and (I assume) the server ends up with its default service principal, HOST/theServer, which is mapped to the machine's Computer account, not a service account. The only way it'll work is if you run your app as a service, because then you can explicitly set the service's login to use the service account's password.

I still have other problems, but at least I got past this!

For more, see [Troubleshooting](https://github.com/Waffle/waffle/blob/master/Docs/Troubleshooting.md)
