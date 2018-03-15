# Struts application not accepting multipart/form-data
----

## Question
There is a legacy system that is using Struts 1.1 and enctype="multipart/form-data" to upload files.  It has been working fine.  I implemented WAFFLE and the file upload no longer works.  When the page is submitted the Action class is called, but the ActionForm properties are empty.   In the ActionForm's reset method the request instance is a MultipartRequestWrapper, however, the ActionMappings are empty.  I remove WAFFLE and the file upload works as normal.  Does anyone know how Struts handles multipart documents and why WAFFLE would interfere with this?

## Answer

> I would imagine that this is a FORM POST. This might be related or interesting: http://code.dblock.org/ShowPost.aspx?id=104. I would get a client-server HTTP trace next.

In the WEB-INF.xml I had an old security constraint that was not commented out.  I removed the security constraint and the file upload works.