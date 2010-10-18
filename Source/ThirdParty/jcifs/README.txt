Thu Oct  7 13:36:57 EDT 2010
jcifs-1.3.15

Adjusted locking on DcerpcHandle routines in jcifs.smb.SID which could
result in "All pipe instances are busy". This is also believed to reduce
occurances of NT_STATUS_TOO_MANY_OPENED_FILES errors (although when
resolving SID names excessively, this error can still occur).

Thu Aug 19 21:05:38 EDT 2010
jcifs-1.3.15a

Fragmented request PDUs have been implemented.

Thu Feb 11 15:10:26 EST 2010
jcifs-1.3.14

A lock has been added to DcerpcHandle to ensure that the NT_CREATE_ANDX
and DCERPC bind are performed together as some pipes will return 'All
pipe instances are busy' errors if the client tries to open the same
pipe concurrently.

JCIFS will no longer do a NetBIOS Node Status to determine the server
hostname as it seems some servers no longer respond to it which causes
a long delay on connect.

Tue Jan  5 13:19:39 EDT 2010
jcifs-1.3.13

Locking throughout the transport layer has been rewritten. This should
fix the long standing deadlock that has been reported in the past. Doubled
size of transient input buffer to accommodate SMB_COM_NEGOTIATE response
security blob (as observed with OSX Snow Leopard). A signing issue reading
data from an EMC server has been fixed. NTLMSSP logging has been improved.

Fri Aug 14 13:45:57 EDT 2009
jcifs-1.3.12

If NtlmPasswordAuthentication.ANONYMOUS was used, CAP_EXTENDED_SECURITY
could be incorrectly turned off resulting in a NullPointerException in
SmbComSessionSetupAndX.java. If a DC does not return any domain referrals,
a NullPointerException could occur. Both of these NPEs have been fixed.

JCIFS could become confused when connecting to a server that also happened
to be a DFS root server. JCIFS will now create separate transports for
these two cases.

Tue Jul 21 11:19:39 EDT 2009
jcifs-1.3.11

The domain was incorrectly upper-cased when performing NTLMv2
authentication. It so happened this was harmless to JCIFS but the nTOWFv2
routine indicates that the domain is not supposed to be upper-cased
(unlike the username which is).

Wed Jun  3 19:42:58 EDT 2009
jcifs-1.3.10

When re-establishing a session an old UID could be set in the
SMB_COM_SESSION_SETUP_ANDX and cause a "The parameter is incorrect"
SmbException. This release explicitly sets the uid to 0 before initiating
a new session which fixes this error.

Fri May 30 00:40:26 EDT 2009
jcifs-1.3.9

* JCIFS will now iteratively try multiple replicated DFS targets if some
  are not enabled (whereas previously JCIFS would quit if the first root
  target was not accessible)
* Fixed "Invalid operation for ????? service" error when querying DFS
* Documented jcifs.smb.client.useExtendedSecurity and that it must be
  set to false when using Samba 3.0.x
* All instances of UnicodeLittleUnmarked have been changed to UTF-16LE
  (for platforms like Android)
* SmbFile.copyTo will now copy files larger than 4GB
* The API documentation has been heavily stripped of unnecessary classes

Sat Mar 29 23:13:29 EDT 2009
jcifs-1.3.8

RC4 has been implemented which eliminates the Java 1.5u7
requirement. JCIFS 1.3 should now work with Java 1.4 (as well as 1.2
did anyway).

Thu Mar 12 14:22:47 EDT 2009
jcifs-1.3.7

Share security with Samba 3.0 was broken. This has been fixed.

Wed Mar 11 20:22:46 EDT 2009
jcifs-1.3.5

Stand-alone DFS did not work with an IP address as opposed to a DNS or
NetBIOS hostname. This issue has been fixed.

Sun Mar  9 11:46:15 EDT 2009
jcifs-1.3.4

JCIFS 1.3.3 was accidentally compiled with Java 1.5. This release has
been compiled with Java 1.4. Note that NTLMv2 still requires Java 1.5
update 7 for RC4.

SMB parameter words were not decoded correctly (which were ultimately
related to the fact that the WordCount of the SMB_COM_NT_CREATE_ANDX
response is wrong because an extra 16 bytes for "offline files /
client side caching (CSC)". Ultimately this caused an error with the
SMB_COM_TREE_CONNECT_ANDX response of certain IBM clusters. This should
now be fixed.

The status codes and text for NT_STATUS_INVALID_COMPUTER_NAME (0xC0000122)
and NT_STATUS_NO_TRUST_SAM_ACCOUNT (0xC000018B) have been added.

Server capabilities in the SMB_COM_NEGOTIATE response were not being
read correctly. This issue has been fixed.

The documentation regarding the return value of renameTo has been fixed.

Sun Jan 25 14:31:31 EST 2009
jcifs-1.3.3

If a jcifs.netbios.wins property is not supplied, the default
jcifs.resolveOrder is now LMHOSTS,DNS,BCAST whereas previously it was
LMHOSTS,BCAST,DNS. This is much more likely to eliminate annoying 6
second timeouts waiting for BCAST queries to timeout.

An "Invalid parameter" error would occur if the RC4 Cipher was not
available for NTLMv2. The logic has been adjusted so that the correct
error is thrown. Note that RC4 is only available in Java 1.5 update 7 and
later. Thus JCIFS 1.3 requires that version or NTLMv2 must be disabled.

The NTLMSSP classes could try to load Cp850 which is not available in
the standard JRE. This has been fixed.

Added setLength method to NdrBuffer class.

Mon Dec 22 13:30:39 EST 2008
jcifs-1.3.2

Querying Samba DFS links directly could fail due to a slight difference
in Samba v. Windows DFS referrals. This issue has been fixed. Querying
Samba DFS roots or paths under DFS links were not affected.

Sat Nov 30 00:32:09 EST 2008
jcifs-1.3.1

The NtlmPasswordAuthentication constructor has been modified to
canonicalize the username from DOMAIN\username or username@domain to
username and set the domain separately.

The getNTLMv2Response method has been adjusted to permit the targetInfo
to be null.

Minor changes to DFS have been applied that claim to prevent errors in
certain DFS scenarios.

The NTLM HTTP Filter was broken in 1.3.0. Turning off useExtendedSecurity
reportedly fixes the issue.

Note: The semantics of the fourth parameter of getNTLMv2Response has
changed.

Sat Oct 25 17:45:51 EDT 2008
jcifs-1.3.0 released

NTLMv2 has been FULLY implemented and is now the default. Signatures
without and without various NTLMSSP flags (e.g. NTLMSSP_NEGOTIATE_NTLM2)
have been tested with Windows 2003 and Windows 2000.

New default values are:

  jcifs.lmCompatibility = 3
  jcifs.smb.client.useExtendedSecurity = true

Note: The NTLM HTTP Filter does not and can never support NTLMv2 as it
uses the main-in-the-middle technique which is specifically thwarted by
factoring the NTLMSSP TargetInformation block into the computed hashes. A
proper NTLMv2 HTTP authentication filter would require NETLOGON RPCs (or
possibly some kind of Kerberos digest authentication like Heimdal uses).

Sun Oct 19 23:25:45 EDT 2008
jcifs-1.2.25

The DcerpcHandle code to increase the stub size if alloc_hint was greater
than stub.length was not being engaged properly which would result in
an ArrayIndexOutOfBoundsException if the DCERPC response was larger than
0xffff. This has been fixed.

Disabled decoding of NativeFileSystem field b/c it seems the iSeries
implementation sends this field in ASCII even though Unicode is
indicated. Fortunately the field is of no interest to anyone so we can
simply omit decoding it.

Added check in copyTo to prevent a possible thread lockup when server
is disconnected during a copy.

Force SMB_COM_TREE_CONNECT_ANDX service to always be what was passed to
the constructor (default is '?????'). IBM iSeries apparently does not
like explicitly specifying 'A:' which can occur on reconnect after
an soTimeout.

Wed Jul 23 13:35:20 EDT 2008
jcifs-1.2.24

The 2 line change that fixes stand-alond DFS was not in 1.2.23. Now it is.

Sun Jul 20 22:28:40 EDT 2008
jcifs-1.2.23

Recent domain-based DFS proper broke stand-alone DFS. This has been fixed.

Wed Jun 25 20:26:33 EDT 2008
jcifs-1.2.22

The SmbFileInputStream methods will now throw InterruptedIOExceptions
where apppropriate whereas previously they would throw SmbExceptions
with a root cause of TransportException with a root cause of
InterruptedException.

If SmbSession.send() throw an exception it could leave the session in a
bad state which could cause "Invalid parameter" exceptions on subsequent
requests.

An InterruptedException in jcifs.netbios.NameServiceClient was being
caught and ignored. It will now be re-thrown as an IOException so that all
threads used with/by JCIFS can be interrupted and caused to exit. Several
other similar (albeit less important) InterruptedExceptions were also
adjusted.

A jcifs.smb.client.dfs.disabled property has been added to disable domain
based DFS so that the client does not try and fail to resolve paths as
domain paths in non-domain environments (e.g. on the local machine).

The getSecurity and getShareSecurity methods will now return null if no
DACL is present on a file whereas previously it would retrun an empty
array. This allows the caller to distinguish between an empty DACL and
one that is simply empty.

Wed May 28 22:46:56 EDT 2008
jcifs-1.2.21

An NPE in jcifs.Config was accidentally introduced in 1.2.20. This has
been fixed.

Tue May 27 16:06:13 EDT 2008
jcifs-1.2.20

The Dfs cache was not thread safe. This has been fixed. The trusted
domains are now looked up with <1C> NetBIOS lookups to speed discovery.

EMC could return "Access denied" for the SMB_COM_FIND_CLOSE2 on a
read-only share. A try catch was added to ignore errors for that request
since it otherwise has no logical importance. Examining a capture of XP
with EMC reveals that no SMB_COM_FIND_CLOSE2 is sent at all.

The read logic could error on the special 0x80000005 status used
by named pipes to indicate more data should be read. This caused
SmbFile.getShareSecurity() to fail on large ACLs. This issue has been
fixed. The getShareSecurity() method would previously fail to resolve more
than about 110 SIDs in one call because of a limitation in JCIFS' ability
to emit multi-fragment request PDUs. The getShareSecurity() method has
been modified to process SIDs in chunks of 64 to work-around this issue.

Sun Apr  6 19:46:47 EDT 2008
jcifs-1.2.19 released

This release adds proper support for domain based DFS roots that are not
hosted on domain controllers and eliminates the now-obsolete behavior of
building a merged list of shares across hosts. A new NetrDfsEnumEx RPC
is used to enumerate DFS roots when listing shares. The equals methods
for SmbFile and UniAddress could return true even though the files were
not equal. That issue has been fixed. Some SmbComOpenAndX parameters
were incorrectly swapped which would cause failure on Windows 98.

Mon Feb 18 23:02:02 EST 2008
jcifs-1.2.18 released

No changes to the code.

Removed docs/.todo.txt.swp that got caught in the tgz by accident.

Wed Feb  6 00:05:42 EST 2008
jcifs-1.2.18e released

The SID.getServerSid() method could fail with NetApp servers due to a
"generic" mask values. The mask has been changed to 0x00000001 which
corresponds to an LsaOpenPolicy mask of POLICY_VIEW_LOCAL_INFORMATION.

The LsaPolicyHandle class would not throw an error if the LsarOpenPolicy2
call failed. This has been fixed.

The SmbFile constructor could inappropriately URL decode the authority
component of SMB URLs.

The NTLM HTTP Filter documentation has been updated.

An Invalid state: 4 error has been fixed.

A NetBIOS name service issue caused by Jetdirect printers has been fixed.

An ArrayIndexOutOfBounds exception in the SmbException class has been
fixed.

A NullPointerException in SmbSession.getChallengeForDomain() has been
fixed.

A NullPointerException in NbtAddress related to hosts without adequate
localhost address configuration has been fixed.

An ArrayIndexOutOfBounds exception could be thrown if a server requires
NTLMv2. This exception has been replaced with a more informative one.

The SmbSessionSetup constructor will now compare the challenge and
encryptionKey using Arrays.equals instead of == to satisfy unforseen
use-cases that otherwise trigger an NT_STATUS_ACCESS_VIOLATION.

If a share was unshared while JCIFS was in the middle of reading files
from it, the transport could enter an error state from which it could
not immediately recover if the share was restored. A small change to
SmbTransport.doRecv() fixes this problem.

Tue Jun 26 16:11:31 EDT 2007

The DCERPC bind did not exactly mimic Windows which uses
SMB_COM_{WRITE,READ}_ANDX. We were using TransactNmPipe throughout which
could result in an 'Incorrect function' error when querying the LSA on
a NetApp server. JCIFS now implements the bind exactly like Windows to
help ensure compatibility with other servers.

A minor performance flaw in the DCERPC code was found and fixed.

Wed Jun 20 13:09:10 EDT 2007
jcifs-1.2.14 released

A new SID.getGroupMemberSids() method has been added that will return
the local group membership of SID (aka aliases). This release adds the
SAMR interface to the dcerpc code with the SamrEnumerateAliasesInDomain
RPC and numerous other calls to negotiate the necessary policy handles.

Mon Jan 22 15:26:01 EST 2007
jcifs-1.2.13 released

A new SmbFile.getShareSecurity() method that uses a new
MsrpcShareGetInfo/ShareInfo502 RPC has been added. This will return the
ACL for a share as opposed to the ACL for the directory shared. See the
API documentation for details. Several DFS issues have been identified
and fixed. If JCIFS receives a NoRouteToHostException on port 445 it
will now try to fallback to port 139. This code has been tested fairly
well already. There have been no changes since b4.

Mon Jan 15 15:47:47 EST 2007
jcifs-1.2.13b4 released

When trying to connect to port 445 some environments can generate a
NoRouteToHostException as opposed to a ConnectException even though
falling back to port 139 would have worked. The SmbTransport class has
been modifed to also catch the NoRouteToHostException and retry with
port 139.

Mon Jan  8 02:26:56 EST 2007
jcifs-1.2.13b3 released

Two DFS bugs introduced after recent changes have been repaired. The
getDfsPath method would return a path with an extra slash (/) if the
directory referred to the DFS root. The listFiles methods could return
the directory itself as a child. Both issues have been fixed.

Fri Jan  5 16:24:27 EST 2007
jcifs-1.2.13b2 released

The DcerpcHandle.sendrecv() code did not properly buffer fragmented
response PDUs. This resulted in an "invalid array conformance" exception
in the NDR routines. This error has been fixed.

Thu Jan  4 18:12:34 EST 2007
jcifs-1.2.13b1 released

A new SmbFile.getShareSecurity() method that uses a new
MsrpcShareGetInfo/ShareInfo502 RPC has been added. See the API
documentation for details. Also, DFS issues have been identified and
fixed.

Wed Dec 27 19:15:27 EST 2006
jcifs-1.2.12 released

Just made 1.2.12b2 final.

Thu Dec 21 12:20:14 EST 2006
jcifs-1.2.12b2 released / getSecurity Bugfix

The NtTransQuerySecurityDesc request could specify a data buffer that
could be too small for the response. As a result the response was not
decoded properly and an error would occur. The response will now be
decoded properly if the buffer is too small and the buffer size has been
increased from 4096 to 32768.

Thu Dec 14 21:01:46 EST 2006
jcifs-1.2.12b released / DFS Bugfix and SID Adjustments Again

The getSecurity() method did not work over DFS. A very small but
potentially significant change has been made to the DFS code. I do not
have a sophisticated DFS test environment so please pay special attention
to JCIFS with DFS and report any problems to the JCIFS mailing list.

The toString() method of the SID class has been changed back to the
old behavior of returning only the numeric SID representation. This
was done not only for backward compatibility with previous versions of
JCIFS but because conceptually the textual representation of a SID is
not it's resolved account name. A new toDisplayString method has been
added to return the resolved Windows ACL editor text (as toString() did
in the 1.2.11 release). The toSidString() method has been removed. The
getDomainName() and getAccountName() methods have not changed.

Sat Dec  9 01:09:43 EST 2006
jcifs-1.2.11 released / SID Class Adjustments

The 1.2.11 release is now final. No serious problems have been reported
with the new SID resolution code however some minor adjustments have been
made with respect to values returned when a SID has not been resolved
(e.g. the associated account was deleted). The SID class API documentation
has been updated accordingly.

Wed Nov 29 11:34:01 EST 2006
jcifs-1.2.11b released / SID Resolution

This release significantly expands the SID and ACE classes by using the
new MSRPC infrastructure to resolve SIDs to their associated account
names. A new SmbFile.getSecurity() method has been added which if called
with a boolean value of true will resolve the SIDs returned within the
ACE[] array such that SID.toString()/getDomainName()/getAccountName()
will return text about the account associated with that SID suitable for
display to users (e.g. MYDOM\alice, SYSTEM, etc). Documentation for all
associated methods and classes has been added.

Fri Nov 24 11:58:14 EST 2006
jcifs-1.2.10 released / Minor Adjustments

The 1.2.10 release is now final. A NetBIOS name service lookup bug has
been fixed in addition to severl other harmless adjustments.

Tue Nov 14 12:32:23 EST 2006
jcifs-1.2.10b released / MSRPC Support, Long Unicode Share Name
  Enumeration and Critical Bugfixes

This release contains the following new functionality and fixes:

  * Long Unicode Share Enumeration - The SmbFile.list* methods will now
    try to use MSRPC to enumerate shares if the target is a server. If
    the operation should fail for any reason, the client will fall
    back to trying the older RAP method. This should permit enumerating
    shares with names that use charsets other than the negotiated OEM
    "ASCII" encoding, share names that are longer than 12 characters,
    and arbirarily large lists of shares.
  * MSRPC Support - MSRPC support has been integrated into JCIFS
    directly. It should now be possible to add new RPCs (AT jobs,
    SID/group name resolution, service management, regedit, etc)
    relatively easily with little knowledge of MSRPC protocols. Look at
    the jcifs/dcerpc/msrpc/MsrpcShareEnum.java class for an example and
    ask the mailing list for further instructions.
  * Apr 24 bugfix - A NullPointerException caused by an error in logic
    has been fixed.
  * May 10 bugfix - The client will now detect if the JRE supports Cp850
    and set the default jcifs.encoding to US-ASCII if it does not. This
    will eliminate some NullPointerExceptions that were occuring as
    a result.
  * A small update about keep-alives has been added to the NTLM HTTP
    Authentication document.
  * Jun 21 bugfix - CLOSE-WAIT sockets left over by read errors have
    been fixed.
  * Jul 19 bugfix - Errors caused by using UnicodeLittle as
    opposed to UnicodeLittleUnmarked have been fixed by ensuring
    UnicodeLittleUnmarked is used throughout the codebase.
  * Oct 3 bugfix - Invalid state errors from Transport classes have
    been fixed. It should be safe to interrupt() JCIFS operations now.
  * Oct 20 bugfix - Uncontrolled looping due to invalid Transport
    logic has been fixed.
  * Oct 25 bugfix - Logic has been added to make domain controller
    lookups more robust.
  * Oct 27 bugfix - Failure when using SmbFile.renameTo() with
    jcifs.smb.client.ssnLimit=1 has been fixed.
  * Oct 31 bugfix - Endless looping when all WINS servers in a list
    are unavailable has been fixed.

Note the openFlags used with SmbFile, SmbNamedPipe, and various streams
classes have been tweaked. They are now the following:

  Flags for SmbFile{In,Out}putStream are:
  bits     meaning
  0-15     open flags (e.g. O_RDWR)
  16-31    lower 16 bits of access mask shifted up 16 bits
  
  Flags for SmbNamedPipe are:
  bits     meaning
  0-7      open flags (e.g. O_RDWR)
  8-15     pipe type (e.g. PIPE_TYPE_CALL)
  16-31    lower 16 bits of access mask shifted up 16 bits

Tue Apr  4 15:44:43 EDT 2006
jcifs-1.2.9 released / Java 1.5 Compiler Issue

The 1.2.8 release was compiled with Java 1.5.0_06. Under certain conditions
this  could  cause  an  error:  <tt>java.lang.LinkageError: ... Unsupported
major.minor  version 49.0</tt>. I have rebuilt the entire package using the
compiler I used previously (1.4.2_08). 

Fri Mar 24 23:14:35 EST 2006
jcifs-1.2.8 released / Deadlock Fix, ACLs, DFS, NTLM HTTP Filter, and More

There are several significant changes in this release. These include:

 o A deadlock could occur if the client tried to logoff and logon at
   the same instant. This has been fixed.
 o It was discovered that in at least some cases "preauthentication"
   did not work properly. A very simple logical error would cause the
   wrong signing digest to be installed. This has been fixed.
 o The ACL patch has been integrated. The SmbFile.getSecurity() method
   is now available.
 o The jcifs.smb.client.responseTimeout and jcifs.smb.client.soTimeout
   values have been increased from 10000ms and 15000ms to 30000ms and
   35000 respectively. Users with crawler type applications will almost
   certainly want to reduce these values.
 o Several logical errors in DFS referral handling have been fixed
   (still no fix for the mystery preemtive behavior observed by Windows
   clients).
 o Documentation has been updated significantly.

Fri Nov 18 17:08:56 EST 2005
jcifs-1.2.7 released / Transport Error, Filter Changes, Integer Overflow,
User Contributed Patches, and More

This release consists of the following changes:

 o Some debugging printlns left over from the last release have been
   removed.
 o Added setContentLength(0) to two other places for the NTLM HTTP
   Filter. This is required for HTTP 1.0 clients (e.g. Google appliance
   servers).
 o The name service code will now properly resolve DNS names that begin
   with digits.
 o Several instances of possible integer overflow have been fixed.
 o A patch for large read and write support has been added to the patches
   directory. A patch for reading security descriptors has been added
   to the patches directory.
 o If a transport was in error due to a connection timeout it could
   remain in the error state indefinitely. This issue has been fixed.

Fri Oct  7 19:47:53 EDT 2005
jcifs-1.2.6 released / Session Management and Filter Fix

It  was  discovered  that redundant sessions could be created. This problem
has  been  fixed  but  the  fix  is to not remove sessions from the list of
sessions  for a transport which is somewhat of a waste of memory. This will
probably  need  to  be  revisited. It has been advised that the Filter call
setStatus() before setContentLength(0). This change has been implemented. 

Fri Sep 30 23:28:51 EDT 2005
jcifs-1.2.5  released  /  Filter Exceptions, Stressing the Transport Layer,
  and DFS Deadlock Fixed 

This release of the JCIFS client consists of the following changes.

o It  was discovered that a flaw in session expiration could cause sessions
  to expire prematurely. This has been repaired.
o If   the   jcifs.netbios.hostname   property  is  set,  the  client  will
  communicate  using  only  NetBIOS  over  port  139.  This is required for
  environments that implement a policy restricting users to logging in from
  certain computers.
o Under  stress  the  client  could  incorrectly attempt to use the invalid
  "NULL" transport. This has been fixed.
o Filter  users  could experience exceptions due to using the port 0 rather
  than the default CIFS port.
o The  client  should  now  handle partial reads and socket exceptions more
  gracefully.
o Under  stress, the DFS referral query could cause the client to deadlock.
  This has been fixed. 

Wed Sep 21 01:53:28 EDT 2005
jcifs-1.2.4  released / Timeout Transport Exception, Bogus Signature Error,
  and More 

A  NetBIOS  keep-alive  message  (received  after  ~10 minutes) would break
message  processesing  with a timedout waiting for response Exception. This
has been fixed.

JCIFS  would  fail  to  validate  responses with a status that is not zero.
Assuming  we are calculating the verfication signature correctly I can only
assume  the  affected  servers choose not to generate the correct signature
for  error  responses  (perhaps for DOS reasons). Because JCIFS checked the
signature  before  the  message  status,  an error response would fail with
"signature  verification  failure".  This behavior has been changed so that
signatures are not verified if the status is non zero.

It  was  discovered  that the new transport (as of 1.2.x) could not cleanly
recover  from  temporary  server  failure  (e.g.  a restart). This has been
fixed. Methods will still throw Exceptions but moment the server comes back
online the client gracefully recover. 

Wed Aug 24 13:29:44 EDT 2005
jcifs-1.2.3 released / Port 445 Fixed

A  mistake  in  the 1.2.2 release broke port 445 communication entirely. It
has  been  fixed.  The  exact error (with a sufficiently high loglevel) was
"Invalid payload size: 1". 

Sat Aug 20 00:26:11 EDT 2005
jcifs-1.2.2  released  / Exception  "cannot  assign requested address" Fixed,
  Clusters, NetApp Filer, and More 

There have been a number of small fixes. These are:

o The  "cannot  assign  requested address" exception caused by trying to bind
  the local address 127.0.0.1 has been fixed.
o In  a  cluster  environment  the  NTLM HTTP Filter could fail with "account
  currently  disabled"  or  "Access  denied"  errors due to a deserialization
  issue  of  "preauthentication"  credentials  stored in the HttpSession. The
  initialization  of  default  credentials has been changed however it is not
  clear  that  the  change  will have any effect as I do not have a clustered
  environment in which to test.
o The  combination  of  plain text passwords and Unicode (largely specific to
  Samba 3) has been fixed.
o A  bogus debugging statement has been discovered and removed. Who left that
  in there?!
o A  Socket.shutdownOutput()  call  has  been  added to doDisconnect as it is
  believed  to  reduce  spurrious  RST frames observed when abruptly shutting
  down  transports.  These  are  believed  to  be harmless but they have been
  associated with unsightly messages in Samba log files.
o The  copyTo()  method  will now check to see if the source path is a child,
  parent  or  equal  to  the  destination  path  and if so throw a Source and
  destination paths overlap exception.
o An  additional  debugging  statement has been added to the NTLM HTTP Filter
  domain controller interrogation code.
o The  getDiskFreeSpace  call  could  fail  with  NetApp  Filer.  It has been
  repaired. 

Sun Jul  3 23:33:03 EDT 2005
jcifs-1.2.1 released

The  SMB  signing  code was totally broken in the last release. It has been
reparied.  The  setAttributes  method did not work on directories. This has
been  fixed  and  the masks used to filter setAttributes/getAttributes have
been  optimized  to allow getting and setting all possible attributes based
on   observed  XP  behavior.  The  getType()  method  would  always  return
TYPE_SHARE  if the SmbFiles were obtained through the listFiles() method on
a  workgroup  or  server URL. This issue has been fixed - getType() may now
return TYPE_PRINTER and TYPE_NAMEDPIPE. 

Sun May 22 18:22:32 EDT 2005
jcifs-1.2.0 released

This release is jcifs-1.1.11trans2 with the following modifications.

Named  pipes  were broken when DCE transactions where added with 1.x. Call,
Transact,  CreateFile,  and  DCE  named  pipe  calls should now all work as
expected.  The  NetBIOS name resolution code will now use the last resource
record of a name query response if there are more than one. This appears to
be more correct in at least one instance (VMWare adapters on my workstation
at work are appearing first).

Also note the trans releases below. 

Mon May  9 18:49:24 EDT 2005
jcifs-1.1.11trans2 released

Socket  exception  handling  was  non-existant and reads would actually not
read anything but 0's. These issues and other small issues have been fixed.

Wed May  4 22:31:28 EDT 2005
jcifs-1.1.11trans released

This  "transitional" release has all the 1.1.10 and 1.1.11 fixes as well as
more  work  on  the transport layer. The last trans release had a silly mid
rollover  bug.  I  have  also emiminated a deadlock condition. These issues
have  been  fixed.  Also  the  client  will  not  properly try port 445 and
fallback  to  139  as necessary. This *could* be stable enough that I might
try to promote this to 1.2.0. 

Thu Apr  7 23:02:48 EDT 2005
jcifs-1.1.9trans released

This  is  a 'transitional' or 'transport rewrite' release. It's stock 1.1.9
but  the  transport layer has been refactored and reduced (actually totally
rewritten  -  SmbTransport.java is less than half the size of it's previous
version).  It  may  still  not be "correct" because I believe the high-load
concurrency  issue  may have to do with how sessions and trees are created.
That  is  another step that delves into how Principles will be handled so I
thought  I  would  release  this  as is because it seems pretty stable so I
thought  I  would  put it out there as a reference point. To give people an
insentive  to  actually  use it I have changed the port to 445, applied the
share reconnect fix from Darren and the getDiskFreeSpace patch from Thomas.
Also if you really need the dial to go to 11, preliminary testing indicates
this transport is a few percent faster. 

Feb 28 03:09:31 EST 2005
jcifs-1.1.9 released

When  multiplexing  I/O, if socket buffers fill up such that packets can be
read  in fragments (i.e. high load), it was possible for the 4 byte NetBIOS
header  to be read incorrectly resulting in a bogus "unexpected EOF reading
netbios  session header" exception. This problem has been fixed. Also, some
small javadoc updates have been applied. 

Thu Feb 10 22:29:12 EST 2005
jcifs-1.1.8 released

The  blocked  thread  bug  wasn't quite fixed in the last release. A lookup
exception  (e.g. caused by an unresponsive domain controller) could leave a
thread  blocked  if  many  requests  are  being  processed  simultaneously.
Similarly  the  fix for the DC lookup code wasn't complete enough to handle
the  unusual  scenario  where  all  DCs  are unresponsive. Also, a malfomed
NetBIOS  name  query  response  could cause the name service thread to exit
incorrectly.  These  issues  have  been fixed. Finally, the URL handling of
smb://@/ (meaning "null" credentials) has been fixed. 

Sun Jan 16 17:30:17 EST 2005
jcifs-1.1.7 released

A  bug  introduced  in  a  recent  release that could cause threads to wait
indefinately  has  been  fixed.  After  time  many threads could be blocked
resulting  in  wasted  resources.  The  DC lookup code has been modified to
gracefully  handle  WINS  returning  an  empty  list (e.g. due to temporary
network failure). A simple fix has been applied that premits SMB signatures
to work without specifying preauthentication credentials. The getAttributes
method  will  now  return 31 bits of attributes whereas previously it would
mask  off the lower 6 bits that JCIFS actually makes use of. A bug has been
fixed  that under certain conditions prevented copyTo() from copying entire
shares.  A try/catch block has been added to copyTo() to permit the copy to
continue if an error occurs. 

Mon Dec 27 17:53:42 EST 2004
jcifs-1.1.6 released

If  a variable length 8 bit encodings such as Big5 is used the NTCreateAndX
command    could    fail.    This    bug    has    been    fixed.   If   an
SmbFile{Input,Output}Stream  was closed, a subsequent operation could cause
the  file to be reopened. This behavior is now blocked such that operations
performed   on  a  stream  after  it  has  been  closed  will  generate  an
IOException.  Some  transport  layer  synchronization  has been adjusted. A
getPrincipal  method  has  been  added  to  SmbFile  that  will  return the
NtlmPasswordAuthentication  object  used  to  create  the file or pipe. The
documentation has been updated regarding transparent NTLM authentication in
Mozilla, the available method of SmbFileInputStream. 

Thu Dec 16 21:57:23 EST 2004
jcifs-1.1.5 released

It was discovered that an ArrayIndexOutOfBoundsException could occur if the
list  of domain controllers returned by NbtAddress.getAllByName was shorter
than  the  list  returned  in  the previous call (possibly because the WINS
query  timed  out  and switched to an alternate WINS server). All NTLM HTTP
Authentication Filter users should upgrade to prevent this error. Also, the
value  of  jcifs.netbios.cachePolicy  set  by the NTLM HTTP Filter if it is
not specified has been doubled to 20 minutes. Finally, some log levels have
been  increased  such that running with jcifs.util.loglevel = 3 temporarily
is  actually  reasonable in a production environment (must use loglevel > 3
to see individual SMB messages and loglevel > 5 to get hexdumps). 

Tue Dec  7 18:34:35 EST 2004
jcifs-1.1.4 released

Two  bugs  regarding  the  upcasing of domain and username fields with LMv2
authentication   (used   with   lmCompatibility   =  3)  have  been  fixed.
Additionally  the  firstCalledName/nextCalledName  methods changed in 1.1.0
have been changed back to the old behavior. The change was not warranted as
it did not emulate Windows behavior. 

Tue Nov 30 19:20:57 EST 2004
jcifs-1.1.3 released

A concurrency error was introduced with the getChallengeForDomain code used
by the NTLM HTTP Filter. This has been fixed. 

Sun Oct 31 00:58:04 EDT 2004
jcifs-1.1.1 released

The  jcifs.smb.client.logonShare  (and  thus  the JCIFSACL NTLM HTTP Filter
example)  did  not  work. It would not restrict users to those found in the
ACL it would permit all authenticated users. This has been fixed.

A  bug  was  discovered  and  fixed  in  the named pipe code. If a specific
sequence  of reads were performed the pipe could become corrupted. This fix
is necessary for multi-pdu DCE requests to work.

A small bug in the new NbtAddress.getAllByName method has been repaired. It
will now broadcast for a name if a WINS address was not provided. 

jcifs-1.1.0 released

The behavior of the firstCalledName/nextCalledName methods has been changed
to try SMBSERVER* first, then the NetBIOS hostname, then the 0x20 name from
a Node Status. It is pretty universal now that SMBSERVER* rules the day and
most  servers  return  failure  with  the  NetBIOS  name  so  this behavior
eliminates a round trip during session establishment.

The  NbtAddress.getByName method has been implemented. This will return the
full  list of RDATA for a name query response. Currently I believe only the
0x1C  domain  lookup  actually  returns  multiple  results.  Note  this  is
different from getAllByAddress which does a node status.

The  socket code in SmbTransport has been modified to open the socket using
the transport thread. This permits the caller of the transport to call wait
for  RESPONSE_TIMEOUT.  This is great if your application has a tendency to
try  to connect to hosts that do not exist. Normally that would take over a
minute  to  timeout. The single threaded SmbCrawler actually performs quite
well with the right properties set.

An SmbSession.getChallengeForDomain() method has been added that returns an
NtlmChallenge  object containing the byte[] challenge and UniAddress of the
domain  controller  from  which  it came. This method will rotate through a
list  of  at  most  jcifs.netbios.lookupRespLimit  addresses  and will only
return  a  challenge  for a responsive server. Unresponsive servers will be
removed from the list until the jcifs.netbios.cachePolicy has expired. This
function  is  used  by  the  NTLM  HTTP  Filter  to  locate suitable domain
controllers.

Because  of  the above rotation there is a greater potential for transports
to  remain  open. Sessions with no activity (this is particularly true with
the  NTLM  HTTP  Filter which really only touches the session once when the
user is authenticated) will be logged off after jcifs.smb.client.soTimeout.

A  read  bug that only manafested itself with a certain EMC server has been
fixed. 

Mon Sep  6 20:44:14 EDT 2004
jcifs-1.0.1 released

The  GUEST  account  fix  broke  guest  access  entirely  for machines that
deliberately  want  it.  So  this  is  the  original  fix but with the test
condition corrected. 

Mon Sep  6 14:59:26 EDT 2004
jcifs-1.0.0 released

Other  than  minor  changes  in  packaging  this code is identical to 0.9.8
released  3 days ago. From now one all development will continue in the 2.0
(?) branch so that the 1.x series remains as stable as possible. 

Thu Sep  2 18:45:35 EDT 2004
jcifs-0.9.8 released

If  the  special  "GUEST"  account is not disabled (almost always is) it is
possible  for  a  bogus  username  to  be  authenticated successfully. This
problem was only partially fixed previously. A clause was incorrectly added
that  was  intended  to  allow  the  username  "guest"  to be authenticated
successfully.  It  is  now  not possible for "guest" to be authenticated at
all.

A  log  message  has  been  added to the NtlmHttpFilter that will be logged
whenever  an  SmbAuthException is triggered and the jcifs.util.log.loglevel
is  greater  than 1. For example, to enable logging authentication failures
with the filter add the following to the filter section in your web.xml. 

    <init-param>
        <param-name>jcifs.util.loglevel</param-name>
        <param-value>2</param-value>
    </init-param>

An  ArrayIndexOutOfBoundsException  that  could occur if NTLMv2 is used but
lmCompatibility was not set to 3 accordingly has been fixed. 

Tue Aug 10 21:25:03 EDT 2004
jcifs-0.9.7 released

It  was  decided that the NTLM HTTP Filter should not set Connection: close
headers,  a  new  SmbFile  constructor has been added and a rogue debugging
statement has been removed. 

--8<--

                                JCIFS
                     The Java CIFS Client Library
                        http://jcifs.samba.org

JCIFS  is  an  Open  Source  client  library  that  implements the CIFS/SMB
networking  protocol  in  100%  Java.  CIFS  is  the  standard file sharing
protocol  on  the  Microsoft Windows platform (e.g. Map Network Drive ...).
This client is used extensively in production on large Intranets. 

REQUIREMENTS:

JCIFS jar file - http://jcifs.samba.org/src/
Java 1.3 or above - http://java.sun.com/products/

INSTALLATION:

Just  add  the  jar  file to you classpath as you would with any other jar.
More specifically:

UNIX:

Go  to  http://jcifs.samba.org and download the latest jar. If you download
the  tgz archive you also get the source code and javadoc API documentation
(see  critical properties discussed on the Overview page). Put it someplace
reasonable and extract it. For example: 

  $ gunzip jcifs-1.0.0.tgz
  $ tar -xvf jcifs-1.0.0.tar

Add  the  jar  to  your classpath. There are two ways to do this. One is to
explicitly set it on the command line when you run your application like:

  $ java -cp myjars/jcifs-1.0.0.jar MyApplication

but  a  more  robust  solution  is  to  export  it  in  your  ~/.profile or
~/.bash_profile like: 

  CLASSPATH=$CLASSPATH:/home/produser/myapp/myjars/jcifs-1.0.0.jar
  export CLASSPATH

WINDOWS:

Go  to  http://jcifs.samba.org and download the latest jar. If you download
the  zip archive you also get the source code and javadoc API documentation
(see  critical properties discussed on the Overview page). Put it someplace
reasonable and extract it with something like Winzip.

Add  the  jar  to  your classpath. There are two ways to do this. One is to
explicitly set it on the command line when you run your application like:

  C:\> java -cp myjars\jcifs-1.0.0.jar MyApplication

but  a  more robust solution would be to change your system environment but
I'm not confident I can tell you accurately how to do that.

It  is  also  common  that  the CLASSPATH be specified in a shell script or
batch file. See the build.bat batch file that runs the Ant build tool as an
example.

USING JCIFS:

In  general  the  public  API  is  extremely simple. The jcifs.smb.SmbFile,
jcifs.smb.SmbFileInputStream, and jcifs.smb.SmbFileOutputStream classes are
analogous     to    the    java.io.File,    java.io.FileInputStream,    and
java.io.FileOutputStream  classes so if you know how to use those it should
be  obvious how to use jCIFS provided you set any necessary properties(such
as WINS) and understand the smb:// URL syntax.

Here's an example to retrieve a file: 

  import jcifs.smb.*;

  jcifs.Config.setProperty( "jcifs.netbios.wins", "192.168.1.230" );
  SmbFileInputStream in = new SmbFileInputStream(
       "smb://dom;user:pass@host/c/My Documents/report.txt" );
  byte[] b = new byte[8192];
  int n;
  while(( n = in.read( b )) > 0 ) {
      System.out.write( b, 0, n );
  }

You can also write, rename, list contents of a directory, enumerate shares,
communicate with Win32 Named Pipe Servers, ...etc.

The  protocol  handler  for  java.net.URL  is also in place which means you
retrieve  files  using the URL class as you would with other protocols. For
example: 

  jcifs.Config.registerSmbURLHandler(); //ensure protocol handler is loaded
  URL url = new URL( "smb://dom;user:pass@host/share/dir/file.doc" );
  InputStream in = url.openStream();

This  will  also work with whatever else uses the URL class internally. For
example  if you use RMI you can serve class files from an SMB share and use
the codebase property:

  -Djava.rmi.server.codebase=smb://mymachine/c/download/myapp.jar

There  are many example programs in the jcifs_1.0.0/examples/ directory. To
execute the Put example you might do: 

  $ java -cp examples:jcifs-1.0.0.jar -Djcifs.properties=jcifs.prp \
                  Put smb://dom;usr:pass@host/share/dir/file.doc
  ##########
  582K transfered

See the API documentation and supplimentary documents in the docs directory
or on the JCIFS website. 

BUILDING JCIFS FROM SOURCE:

The Ant build tool is required to build JCIFS:

   http://jakarta.apache.org/ant/

After  installing Ant just run 'ant' in the JCIFS directory. It should read
the  build.xml  and  present  a  list  of targets. To build a new jar after
modifying the source for example simply type 'ant jar'. 

ACKNOWLEDGEMENTS

Special thanks to Eric Glass for work on NTLM, the NTLM HTTP Authentication
Filter,  and  RPCs.  Thanks  is  also  due  to  the  Samba organization and
Christopher  R.  Hertel  for starting the JCIFS project. Finally, thanks to
users who diagnose problems and provide the critical feedback and solutions
that make the Open Source model great. 
