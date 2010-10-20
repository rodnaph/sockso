<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Sockso - {if $title}{$title|escape}{else}Personal Music Server{/if}</title>
<meta name="keywords" content="" />
<meta name="description" content="" />
<link href="{$smutty->baseUrl}/default.css" rel="stylesheet" type="text/css" />
</head>
<body>
<!-- start header -->
<div id="header">
	<div id="logo">
		<h1>{link url={ controller="index" } text="Sockso"}</h1>
	</div>
	<div id="menu">
		<ul>
			<li>{link url={ controller="index" } text="home"}</li>
			<li><a href="http://forums.pu-gh.com/">forums</a></li>
			<li>{link url={ controller="manual" } text="manual"}</li>
			<li>{link url={ controller="source" } text="source"}</li>
			<li>{link url={ controller="contact" } text="contact"}</li>
			<li>{link url={ controller="downloads/" } text="archive"}</li>
		</ul>
	</div>
</div>
<!-- end header -->
<div id="headerbg"><p class="text1">&#8220;A personal music server<br />
	 for everyone&#8221;</p>
</div>
<!-- start page -->
<div id="page">
    <!-- start content -->
    <div id="content">
        <!-- start latest-post -->
        <div id="latest-post" class="post">
