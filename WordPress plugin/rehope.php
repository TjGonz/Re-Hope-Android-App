<?php
/*
Plugin Name:    Re:Hope Mobile Apps XML-RPC Methods
Plugin URI:     http://rehope.co.uk
Description:    XML-RPC Methods for Re:Hope's Mobile Apps, to glorify Jesus!
Version:        1.0
Author:         Dan Roundhill
Author URI:     http://danroundhill.com
 */

add_filter( 'xmlrpc_methods', 'add_new_xmlrpc_methods' );
function add_new_xmlrpc_methods( $methods ) {
    $methods['rehope.getHomeTabData'] = 'get_home_tab_data';
	$methods['rehope.getEvents'] = 'get_events';
	$methods['rehope.getMessages'] = 'get_messages';
	
	//for testing
	$methods['rehopeTest.getEvents'] = 'get_events_test';
    return $methods;
}

function get_home_tab_data( $args ) {
	//get the 3 latest posts from the 'Featured' category
	 $lastposts = get_posts('numberposts=3&category=4');
	 $ctr = 0;
	 foreach( $lastposts as $post ) {
		$data[$ctr]['image'] = get_post_image_url( $post->post_content );	
		$data[$ctr]['link'] = get_post_meta($post->ID, 'link', true);
		$ctr++;
	 }

    return $data;
}

function get_events( $args ) {
	//get the 10 latest posts from the 'Events' category
	 $args = array( 'numberposts' => 20, 'category' => 5, 'meta_key' => 'date', 'orderby' => 'meta_value', 'order'=> 'ASC');
	 $lastposts = get_posts($args);
	 $ctr = 0;
	 foreach( $lastposts as $post ) {
		$date_str = get_post_meta($post->ID, 'date', true);
		if ( (strtotime( $date_str ) + 86400) > time() ) {
			$events[$ctr]['image'] = get_post_image_url( $post->post_content );
			$events[$ctr]['title']  = $post->post_title;
		    $events[$ctr]['content'] = $post->post_excerpt;
			$events[$ctr]['date']  = $date_str;	
			$events[$ctr]['city_link']  = get_post_meta($post->ID, 'city_link', true);
			$ctr++;	
		}
	 }

    return $events;
}

function get_events_test( $args ) {
	//get the 10 latest posts from the 'Events' category
	 $args = array( 'numberposts' => 20, 'category' => 5, 'meta_key' => 'date', 'orderby' => 'meta_value', 'order'=> 'ASC');
	 $lastposts = get_posts($args);
	 $ctr = 0;
	 foreach( $lastposts as $post ) {
		$date_str = get_post_meta($post->ID, 'date', true);
		if ( (strtotime( $date_str ) + 86400) > time() ) {
			$events[$ctr]['image'] = get_post_image_url( $post->post_content );
			$events[$ctr]['title']  = $post->post_title;
		    $events[$ctr]['content'] = $post->post_excerpt;
			$events[$ctr]['date']  = $date_str;	
			$events[$ctr]['city_link']  = get_post_meta($post->ID, 'city_link', true);
			$ctr++;	
		}
	 }

    return $events;
}

function get_messages( $args ) {
	//get the 10 latest posts from the 'Events' category
	 $lastposts = get_posts('numberposts=10&category=6');
	 $ctr = 0;
	 foreach( $lastposts as $post ) {
		$events[$ctr]['image'] = get_post_image_url( $post->post_content );
		$events[$ctr]['title']  = $post->post_title;
	    $events[$ctr]['content'] = $post->post_excerpt;
		$events[$ctr]['date']  = get_post_meta($post->ID, 'date', true);	
		$events[$ctr]['mp3_url']  = get_post_meta($post->ID, 'mp3_url', true);	
		$ctr++;
	 }

    return $events;
}

// get the first image attached to the current post, return the url
function get_post_image_url( $content ) {
	
	$doc = new DOMDocument();
	    $doc->loadHTML($content);
	    $imageTags = $doc->getElementsByTagName('img');

	    foreach($imageTags as $tag) {
	        return $tag->getAttribute('src');
	    }
}

?>