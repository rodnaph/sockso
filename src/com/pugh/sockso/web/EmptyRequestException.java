
package com.pugh.sockso.web;

/**
 *  it seems to be that IE6 causes Sockso to read empty requests, but I'm really
 *  not sure what is going on at the moment.  So this class encapsulates this
 *  exception.  Could be an IE bug, or a Sockso bug...  or something else ;)
 * 
 *  Clues...
 * 
 *  1) Viewing the headers of the requests IE sends they all complete ok
 *  2) Nothing on the page fails to load
 *  3) It happens a few times right at the end of the page load
 * 
 *  Ideas...
 * 
 *  1) Maybe it's a keep-alive connection of some kind?
 * 
 */

public class EmptyRequestException extends Exception {}
