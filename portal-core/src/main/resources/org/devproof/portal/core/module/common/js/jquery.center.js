jQuery.fn.center = function() {
	var winH = ($(window).height() / 2) - (this.height() / 2) + $(window).scrollTop();
	var winW = ($(window).width() / 2) - (this.width() / 2) + $(window).scrollLeft();
	this.css("top", winH + "px");
	this.css("left", winW + "px");
	return this;
}