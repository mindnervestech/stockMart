var taskApp = angular.module('chatRoom', ['ngResource', 'ngRoute', 'angularFileUpload', 'ngDialog']);


taskApp.controller('ApplicationController', function($scope, $http) {
		
	$scope.getUserPic = function(uId){
		console.log(uId);
		$scope.userImage = '/getUserPic/' + uId;
	}
	
});

taskApp.controller('DashboardController', function($scope, $http, $timeout, ngDialog) {
	
	$scope.loadAllChats = function(userId){
    	console.log(userId);
    	$scope.allChats = [];
    	$http.get('/loadAllChats', {params: {userId: userId }}
    		).then(function(res){
    			console.log(res.data);
    			$scope.allChats = res.data.chats;
    			console.log($scope.allChats);
    			if($scope.allChats.length != 0){
					for(var i=0; i<$scope.allChats.length; i++){
						var el = $('<div style="float:left;padding-left:0px;padding-right:0px;" class="col-xs-1 col-sm-1 col-md-1 col-lg-1"><img id="userPic" style="width:100%;"></div><div class="col-xs-11 col-sm-11 col-md-11 col-lg-11" style="float:right;padding-right:0px;"><div style="font-size:11px;color:darkgray;"><span id="user" class="col-xs-6 col-sm-6 col-md-6 col-lg-6" style="float:left;padding-left:0px;"></span><span style="padding-right:0px;text-align:right;" id="time" class="col-xs-6 col-sm-6 col-md-6 col-lg-6"></span></div><span id="message" style="padding-left:0px;padding-right:0px;" class="col-xs-12 col-sm-12 col-md-12 col-lg-12"></span></div><hr>');
		            	var d = new Date($scope.allChats[i].messageTime);
		            	var res = d.toString().split(" ");
		            	$scope.mTime = res[0] + " " + res[1] + " " + res[2] + " " + res[4] + " IST " + res[3];
		            	//console.log($scope.mTime);
		            	console.log($scope.allChats[i].type);
		            	if($scope.allChats[i].type == "message"){
		         	    	$("#message", el).text($scope.allChats[i].message);
		         	    } else {
		         	    	$("#message", el).text("Attachement");
		         	    }
		            	$("#user", el).text($scope.allChats[i].user);
		            	$("#time", el).text($scope.mTime);
		            	$("#userPic", el).attr('src','/getUserPic/'+$scope.allChats[i].userId);
		            	$('#messages').append(el);
		            	$('#messages').scrollTop(0);
		            	$timeout(function(){
		            		var objDiv = document.getElementById("messages");
		            		objDiv.scrollTop = objDiv.scrollHeight;
		            	})
					}
    			}
    		}, function(error){
    			
    		});
    }
	
	/*$scope.loadAllMembers = function(){
		$scope.allMembers = [];
    	$http.get('/loadAllMembers', {}
    		).then(function(res){
    			$scope.allMembers = res.data;
    			//console.log($scope.allMembers);
    			for(var i=0; i<$scope.allMembers.length; i++){
    				var el1 = $('<div class="img-comment vignette"><img id="userImage"></div><div class="comment-body"><span id="user"></span></div><hr>')
    	        	$("#userImage", el1).attr('src','/getUserPic/'+$scope.allMembers[i].id);
    	        	$("#user", el1).text($scope.allMembers[i].name);
    	        	$("#members").append(el1);
    			}
    		}, function(error){
    			
    		});
	}*/
	
	$scope.showOnline = function(data){
		console.log(data);
		$scope.allMembers = [];
    	$http.get('/loadAllMembers', {}
    		).then(function(res){
    			$("#members").html('');
    			$scope.allMembers = res.data;
		    	for(var i=0; i<$scope.allMembers.length; i++){
					var el1 = $('<div style="float:left;"><img id="userImage" class="user-img"></div><div class="comment-body"><div class="comment-info" style="margin-top:-5px;margin-bottom:-5px;"><span id="user"></span></div><span id="status"></span></div><hr>')
			       	$("#userImage", el1).attr('src','/getUserPic/'+$scope.allMembers[i].id);
			       	$("#user", el1).text($scope.allMembers[i].name);
				    for(var j=0; j<data.length;j++){   	
			       		if(data[j] == $scope.allMembers[i].id){
							$("#status", el1).text("available");
							break;
			       		}
			       		$("#status", el1).text("busy");
			    	}	
			     	$("#members").append(el1);
				}
    		}, function(error){
    			
    		});
	}
	
	
	//$scope.allSymbols = [];
	
	$scope.addToWishlist = function(item){
		console.log(item);
		var flag = true;
		for(var i=0;i<$scope.results.length;i++){
			if(angular.equals($scope.results[i].Symbol, item.Symbol)){
				flag = false;
				break;
			}
		}
		if(flag == true){
			$http.post('/addToWishlist',{name: item.Name,
				symbol: item.Symbol,
				exchange: item.Exchange}
				).then(function(res){
					//console.log(res.data);
					alert("added");
					$scope.getDataResult(res.data.symbol);
				}, function(error){
					
				});
		}else{
			alert("already there.");
		}
	}
	
	$scope.loadAllWishlist = function(){
		$http.get('/loadAllWishlist',{}
			).then(function(res){
				//console.log(res.data);
				for(var i=0; i<res.data.length; i++){
					$scope.getDataResult(res.data[i].symbol);
				}
			}, function(error){
				
			});
	}
    
    $scope.results = [];
    
    $scope.getDataResult = function(symbol){
    	//console.log("ANGULAR JS"+symbol);
    	new Markit.QuoteService(symbol, function(jsonResult) {
    		$scope.$apply(function(){
    			$scope.results.push(jsonResult);
    		});
    		console.log($scope.results);
    	});
    }
    
    $scope.refreshQuote = function(symbol, index){
    	console.log("ANGULAR JS"+symbol);
    	new Markit.QuoteService(symbol, function(jsonResult) {
    		$scope.$apply(function(){
    			$scope.results[index] = jsonResult;
    		});
    		console.log($scope.results);
    	});
    }
    
    $scope.refreshAll = function(){
    	$scope.results = [];
    	$scope.loadAllWishlist();
    	console.log($scope.results);
    }
    
    $scope.removeFromWishlist = function(index){
    	console.log($scope.results[index]);
    	$http.put('/removeFromWishlist',{name: $scope.results[index].Name}
    		).then(function(res){
    			alert("deleted");
    			$scope.results.splice(index,1);
    	    	//console.log($scope.results);
    		}, function(error){
    			
    		});
    }
    
    $scope.getQuote = function(item){
    	console.log(item);
    	$scope.item = angular.copy(item);
    	new Markit.QuoteService(item.Symbol, function(jsonResult) {
    		$scope.$apply(function(){
    			$scope.quoteResult = jsonResult;
    		});
    		console.log($scope.quoteResult);
    	});
    	ngDialog.open({template: 'getQuote', scope: $scope});
    }
    
    $scope.showQuote = function(index){
    	//alert("hiii");
    	console.log($scope.results[index]);
    	$scope.quoteResult = $scope.results[index];
    	ngDialog.open({template: 'getQuote', scope: $scope});
    }
          
});


taskApp.controller('MyAccountController', function($scope, $http, $timeout, $upload) {
	
	$scope.selectedTab = 1;
	
	$scope.uploadUserPic = function(){
		$scope.upload = $upload.upload({
            url: '/uploadUserPic',
            method: 'post',
            fileFormDataName: 'file',
            file: file
        }).success(function (res) {
        	console.log("success");
        });

	}
	
	$scope.img = $scope.userImage;
	
	var file = '';
	$scope.onFileSelect = function ($files) {
        file = $files[0];
        //console.log(file);
        $scope.dataUrls = [];
        console.log("files leng " + $files.length);
        for (var i = 0; i < $files.length; i++) {
            var $file = $files[i];
            if (window.FileReader && $file.type.indexOf('image') > -1) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($files[i]);
                var loadFile = function (fileReader, index) {
                    fileReader.onload = function (e) {
                        $timeout(function () {
                            $scope.img = e.target.result;
                            $scope.dataUrls.push(e.target.result);
                        });
                    }
                }(fileReader, i);
            }
        }
    }

    $scope.postPhoto = function () {
        $("#userPic").click();
    };
	
});

taskApp.controller('ChangePasswordController', function($scope, $http) {
	
	$scope.selectedTab = 2;

	$scope.changePassword = function(){
		console.log($scope.pwd);
		$http.post('/changePassword',{pwd: $scope.pwd}
			).then(function(res){
				console.log("success");
				document.getElementById("pwd").value = '';
				document.getElementById("confPwd").value = '';
			}, function(error){
				
			});
	}
	
});