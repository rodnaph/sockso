
{include file="header.tpl" title="Console - Enabling Uploads"}

<h1>Console - Enabling Uploads</h1>

<p>Enabling uploads via Sockso's GUI is an easy point-and-click affair, but it's
a little more tricky on the server.  But just follow these steps and you'll
have it up and running in no time.</p>

<h3>1) Create Uploads Collection</h3>

<p>First you'll need to create a collection where the uploaded files will be
stored in.  To do this use the <strong>coladd</strong> command to specify
the path to the folder.  Then when it's added run <strong>collist</strong> and
make a note of the number that appears to the left of this new folder (this is
it's collection ID).</p>

<h3>2) Set Properties</h3>

<p>You'll then need to set <i>uploads.enabled</i> to <strong>yes</strong>, and
<i>uploads.collectionId</i> to the collection ID of the folder you just added.
</p>

<p>You can also use the <i>uploads.allowAnonymous</i> to control whether or not
users need to be logged in to upload files. (this defaults to requiring a login
before users can upload)</p>

{include file="footer.tpl"}
