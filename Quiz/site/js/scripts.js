$(document).ready(function() {
	let quiz = {};
	
	let questions = [];
	let question_cur = 0;
	let question_no = 0;
	
	let answer = false;
	let correct = 0;
	let incorrect = 0;
	
	// init: load quizzes
	(function() {
		show("select");
		
		let html = {};
		let i = 0;
		let length;
		
		// create list of JSON files in directory
		let directory = $.get("site/quizzes", function(directory) { return directory; });
		$.when(directory).then(function(directory) { load(directory); });
		
		// load and extract data from JSON files
		function load(directory) {
			let files = $(directory).find("a:contains(.json)");
			length = files.length;
			
			files.each(function(i) {
				let filename = $(this).text();
				$.when($.getJSON("site/quizzes/" + filename, function(json) {
					return json;
				})).then(function(json) {
					extract(json["quiz-id"], json["quiz-name"], filename);
				});
			});
		}
		
		// save data to array and print content to site when completed
		function extract(id, name, filename) {
			html[id] = [name, filename];
			i++;
			if(i === length) {
				$.each(html, function(id, data) {
					$("#main-quiz-select").append(
						"<div class='quiz'>" +
							"<div>" + id + "</div>" +
							"<span>" + parseMarkup(data[0]) + "</span>" +
							"<span class='hidden'>" + data[1] + "</span>" +
						"</div>"
					);
				});
			}
		}
	})();
	
	// start quiz
	$(document).on("click", ".quiz div", function() {
		let filename = $(this).siblings("span:last-child").html();
		$.getJSON("site/quizzes/" + filename, function(json) {
			quiz = json;
			
			questions = [...Array(json["questions"].length).keys()];
			if(json["quiz-type"] === 2) {
				questions = shuffle(questions);
			} else if(json["quiz-type"] === 3)  {
				questions = shuffle(questions.slice(0, json["quiz-qn"]));
			}
			question_cur = 0;
			question_no = questions.length;
			
			correct = 0;
			incorrect = 0;
			
			$("#quiz-name").html(parseMarkup(json["quiz-name"]));
			
			show("play");
			newQuestion();
		});
	});
	
	// replay quiz
	$(document).on("click", "#replay-quiz", function() {
		questions = shuffle([...Array(question_no).keys()]);
		question_cur = 0;
		correct = 0;
		incorrect = 0;
		
		show("play");
		newQuestion();
	});
	
	// select quiz
	$(document).on("click", "#select-quiz", function() {
		show("select");
	});
	
	// shuffle array
	function shuffle(a) {
		let j, x, i;
		for(i = a.length - 1; i > 0; i--) {
			j = Math.floor(Math.random() * (i + 1));
			x = a[i];
			a[i] = a[j];
			a[j] = x;
		}
		
		return a;
	}
	
	// parse markup
	function parseMarkup(a) {
		if(typeof a === "string") {
			return a.replace(/\$_/, "<span class='blank'></span>").replace(/\$~/, "&shy;");
		}
		return a;
	}
	
	// load new question
	function newQuestion() {
		if(question_cur !== question_no) {
			question_cur++;
			updateHeader();
			
			let question = quiz["questions"][questions[question_cur - 1]];
			$("#question").html(parseMarkup(question["question"]));
			if(typeof question["choices"] === "undefined") {
				// load text question
				answer = question["answer"];
				
				$("#input-x").removeClass("correct incorrect").val("");
				$("#input-text").removeClass("hidden");
				$("#input-choice").addClass("hidden");
				setTimeout(function() { $("#input-x").prop("disabled", false); }, 500);
			} else {
				// load choice question
				function check(value) { return (typeof value !== "undefined" ? value : false); }
				
				let row_cd = $("#input-choice table tr:nth-child(2)");
				let row_ef = $("#input-choice table tr:nth-child(3)");
				
				let choices = question["choices"];
				let keys = Object.keys(choices);
				let order = shuffle([...Array(keys.length).keys()]);
				answer = ["a", "b", "c", "d", "e", "f"][order.indexOf(keys.indexOf(question["answer"]))];
				
				let choice_a = parseMarkup(choices[keys[order[0]]]);
				let choice_b = parseMarkup(choices[keys[order[1]]]);
				let choice_c = parseMarkup(check(choices[keys[order[2]]]));
				let choice_d = parseMarkup(check(choices[keys[order[3]]]));
				let choice_e = parseMarkup(check(choices[keys[order[4]]]));
				let choice_f = parseMarkup(check(choices[keys[order[5]]]));
				
				$("#input-a, #input-b, #input-c, #input-d, #input-e, #input-f").removeClass("correct incorrect");
				$("#input-a span:nth-child(2)").html(choice_a);
				$("#input-b span:nth-child(2)").html(choice_b);
				
				if(choice_c !== false) {
					row_cd.removeClass("hidden");
					$("#input-c").removeAttr("colspan").removeClass("hidden").find("span:nth-child(2)").html(choice_c);
					if(choice_d !== false) {
						$("#input-d").removeClass("hidden").find("span:nth-child(2)").html(choice_d);
						if(choice_e !== false) {
							row_ef.removeClass("hidden");
							$("#input-e").removeAttr("colspan").removeClass("hidden").find("span:nth-child(2)").html(choice_e);
							if(choice_f !== false) {
								$("#input-f").removeClass("hidden").find("span:nth-child(2)").html(choice_f);
							} else {
								$("#input-e").prop("colspan", "2");
								$("#input-f").addClass("hidden");
							}
						} else {
							row_ef.addClass("hidden");
						}
					} else {
						$("#input-c").prop("colspan", "2");
						$("#input-d").addClass("hidden");
						row_ef.addClass("hidden");
					}
				} else {
					row_cd.addClass("hidden");
					row_ef.addClass("hidden");
				}
				
				$("#input-choice").removeClass("hidden");
				$("#input-text").addClass("hidden");
				setTimeout(function() { $("#input-choice").removeClass("disabled"); }, 500);
			}
		} else {
			updateHeader();
			
			let score = Math.round((correct / question_no) * 100);
			let message;
			if(score === 100) {
				message = "Super! Alles richtig!";
			} else if(score >= 90) {
				message = "Gut gemacht! Fast perfekt!";
			} else if(score >= 75) {
				message = "Gut gemacht!";
			} else if(score >= 50) {
				message = "Befriedigend! Versuche es noch einmal!";
			} else if(score >= 30) {
				message = "Versuche es noch einmal! Übung macht den Meister!";
			} else if(score >= 10) {
				message = "Übung macht den Meister!";
			} else if(score >= 0) {
				message = "Keine Bange! Übung macht den Meister!";
			}
			
			$("#comment").html(message);
			show("end");
		}
	}
	
	// update header
	function updateHeader() {
		$("#question-no").html("Frage  " + question_cur + " / " + question_no);
		$(".quiz-correct").html(correct);
		$(".quiz-incorrect").html(incorrect);
	}
	
	// show
	function show(id) {
		$(
			"#header-quiz-select, #main-quiz-select, " +
			"#header-quiz-play, #main-quiz-play, " +
			"#header-quiz-end, #main-quiz-end"
		).addClass("hidden");
		$("#header-quiz-" + id + ", #main-quiz-" + id).removeClass("hidden");
		if(id === "play" || id === "end") {
			$("#quiz-name").removeClass("hidden");
		} else {
			$("#quiz-name").addClass("hidden");
		}
	}
	
	// check text answer
	$(document).on("keyup", "#input-x", function(e) {
		if(e.which === 13) {
			let element = $(this);
			element.prop("disabled", true);
			
			let input = element.val().trim().replace(/\s+/g, " ").toLowerCase();
			let timeout;
			if(input === answer) {
				correct++;
				timeout = 1500;
				element.addClass("correct");
			} else {
				incorrect++;
				timeout = 3000;
				element.addClass("incorrect");
			}
			
			updateHeader();
			setTimeout(function() { newQuestion(); }, timeout);
		}
	});
	
	// check choice answer
	$("#input-choice").on("click", "#input-a, #input-b, #input-c, #input-d, #input-e, #input-f", function() {
		if(!$("#input-choice").hasClass("disabled")) {
			let element = $(this);
			$("#input-choice").addClass("disabled");
			let input = element.prop("id").slice(-1);
			let timeout;
			if(input === answer) {
				correct++;
				timeout = 1500;
				element.addClass("correct");
			} else {
				incorrect++;
				timeout = 3000;
				element.addClass("incorrect");
			}
			
			updateHeader();
			setTimeout(function() { newQuestion(); }, timeout);
		}
	});
	
	// open/close user
	$(document).on("click", "#user-toggle", function() {
		let user = $("#user");
		if(user.is(":visible")) {
			user.fadeOut(200, function() { $(this).addClass("hidden"); });
		} else {
			user.hide().removeClass("hidden").fadeIn(200);
		}
	});
	
	// close dialog
	$(document).on("click", ".dialog .close", function() {
		$(this).closest(".dialog").fadeOut(200, function() { $(this).addClass("hidden"); });
	});
	
	// highlight label when textbox, select or toggle is focused
	$(document).on("focusin", ".box > input[type='text']", function() {
		$(this).removeClass("error success");
		$(this).prev().css("color", "#1A73E8");
	}).on("focusout", ".box > input[type='text']", function() {
		$(this).prev().css("color", "");
	});
	
	// add focus line on textboxes
	$("input[type='text'], input[type='email'], input[type='password']").after("<div class='focus-line'></div>");
});