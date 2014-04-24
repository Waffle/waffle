Configuring Browsers for Single-SignOn
======================================

This document explains how to configure various browsers for Integrated Windows Authentication with Kerberos and/or NTLM. 

Internet Explorer
-----------------

Ensure that Integrated Windows Authentication is enabled. 

1. Choose the `Tools`, `Internet Options` menu. 
2. Click the `Advanced` tab. 
3. Scroll down to `Security`.
4. Check `Enable Integrated Windows Authentication`. 
5. Restart the browser.

The target website must be in the `Intranet Zone`. 

1. Navigate to the website.
2. Choose the `Tools`, `Internet Options` menu. 
3. Click the `Local Intranet` icon. 
4. Click the `Sites` button. 
5. Check `Automatically detect intranet network`. 

If the above didn't solve the problem, click `Advanced`. Add the website to the list of Intranet sites.

Firefox
-------

1. Type `about:config` in the address bar and hit enter. 
2. Type `network.negotiate-auth.trusted-uris` in the `Filter` box. 
3. Put your server name as the value. If you have more than one server, you can enter them all as a comma separated list.
4. Close the tab.

[Optional Firefox Plugin]

1. Install integrated auth for firefox from [here](https://addons.mozilla.org/en-US/firefox/addon/integrated-auth-for-firefox/).
2. Following instructions.
