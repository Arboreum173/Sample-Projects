$(document).ready(function() {
	/* -- Dieses Skript kommt von meinem Bruder Philipp Jöns -- */
	/* Burgermenu auf- und einklappen */
	$(document).on("click", ".navigation .burgermenu, .navigation .main", function() {
		if($(".navigation .main").css("position") === "absolute") {
			$(".navigation .main").slideToggle(300);
		}
	});
	
	/* Datenschutz ein- und ausklappen */
	$(document).on("click", "#datenschutz-toggle", function() {
		$("#datenschutz").slideToggle(300);
	});
	
	/* Formular zurücksetzen */
	$(document).on("click", "#zuruecksetzen-1, #zuruecksetzen-2", function() {
		let form = $(this).closest(".form");
		form.find("input, textarea").val("");
		form.find("input:checkbox").prop("checked", false);
		form.find("select").prop("selectedIndex", 0);
	});
	
	/* Swiper definieren */
	let swiper = new Swiper(".swiper-container", {
		navigation: {
			nextEl: ".swiper-button-next",
			prevEl: ".swiper-button-prev",
		}
	});
	
	/* Rezept anzeigen */
	/* BUG: recipes don't update when swiping */
	$(document).on("click", ".swiper .swiper-container .toggle", function() {
		let swiper = $(this).closest(".swiper");
		if(swiper.find(".text").is(":visible")) {
			$(this).html("Rezept anzeigen");
		} else {
			$(this).html("Rezept schließen");
		}
		
		let rezept = swiper.find(".swiper-slide.swiper-slide-active").index() + 1;
		$("#rezept-" + rezept).slideToggle(300);
	});
	
	$(".swiper .swiper-container").on("click", ".swiper-button-next, .swiper-button-prev", function() {
		let text = $(this).closest(".swiper").find(".text");
		let toggle = $(this).closest(".swiper").find(".swiper-container .toggle");
		if(text.is(":visible")) {
			text.slideUp(300);
			toggle.html("Rezept anzeigen");
		}
	});
});