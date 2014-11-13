var taskApp = angular.module('chatRoom', ['ngResource', 'ngRoute', 'angularFileUpload', 'cgNotify']);


taskApp.controller('ApplicationController', function($scope, $http, notify, $interpolate) {
		
	$scope.getUserPic = function(uId){
		console.log(uId);
		$scope.userImage = '/getUserPic/' + uId;
	}
	
	$scope.showOnline = function(data){
		//$scope.allMembers = [];
		$http.get('/loadAllMembers', {}
    		).then(function(res){
    			$scope.allMembers = res.data;
		    	for(var i=0; i<$scope.allMembers.length; i++){
					for(var j=0; j<data.length;j++){   	
			       		if(data[j] == $scope.allMembers[i].id){
							$scope.allMembers[i].status = "online";
							break;
			       		}
			       		$scope.allMembers[i].status = "offline";
			    	}	
			     }
		    }, function(error){
    			
    		});
	}
	
	$scope.allCompanies = [];
	
	$scope.loadPortfolio = function(){
		$scope.portfolioURL="";
		$http.get('/loadPortfolio',{}
			).then(function(res){
				$scope.allCompanies = res.data;
				var fHeight = 300;
				if($scope.allCompanies.length > 0){
					var fCount=$scope.allCompanies.length;
					$scope.portfolioURL = "http://widgets.macroaxis.com/widgets/partnerWatchlistBarsSnap.jsp?gia=t&amp;tid=123&amp;t=44&amp;s="	
						for(var i=0; i<$scope.allCompanies.length;i++){
						if(i<$scope.allCompanies.length-1){
							$scope.portfolioURL = $scope.portfolioURL + $scope.allCompanies[i].symbol + ":" + $scope.allCompanies[i].noOfShares + ",";  
						}else{
							$scope.portfolioURL = $scope.portfolioURL  +$scope.allCompanies[i].symbol + ":" + $scope.allCompanies[i].noOfShares;
						}
					}
					console.log($scope.portfolioURL);
				}
				if(isNaN(fCount)){
					fCount=0;
					}				
				fHeight= fHeight + (fCount*10);
				var url4 = "<iframe bgcolor='#ffffff' id='macroaxis_watchlist_bars' name='macroaxis_watchlist_bars' marginheight='0' marginwidth='0' scrolling='NO' width='100%' height='" + fHeight +"px' frameborder='0' src='" + $scope.portfolioURL + "'></iframe>"; 
				var formattedHTML = $interpolate(url4)($scope);
				$("#myPortfolio").html(formattedHTML);
			}, function(error){
				
			});
	}
	
	$scope.company;
	$scope.noOfShares;
	$scope.addToPortfolio = function(){
		console.log($scope.company);
		$http.post('/addToPortfolio',{name:$scope.company.Name,
			symbol:$scope.company.Symbol,
			noOfShares:$scope.noOfShares}
			).then(function(res){
				$scope.loadPortfolio();
				document.getElementById("symSearch").value = "";
				document.getElementById("noOfShares").value = "";
				notify("Portfolio updated!");
			}, function(error){
				
			});
	}
	
	$scope.updatePortfolio = function(company){
		$http.put('/updatePortfolio',{id:company.id}
			).then(function(res){
				$scope.loadPortfolio();
				notify("Portfolio updated!");
			}, function(error){
				
			});
	}
	
});

taskApp.controller('DashboardController', function($scope, $http, $timeout, notify, $interval) {
	
	$scope.loadAllChats = function(userId){
    	$scope.allChats = [];
    	$http.get('/loadAllChats', {params: {userId: userId }}
    		).then(function(res){
    			$scope.allChats = res.data.chats;
    			if($scope.allChats.length != 0){
					for(var i=0; i<$scope.allChats.length; i++){
						var d = new Date($scope.allChats[i].messageTime);
						d.setHours(d.getHours()-10);
						d.setMinutes(d.getMinutes()-30);
		            	var res = d.toString().split(" ");
		            	$scope.allChats[i].messageTime = res[1] + " " + res[2] + " " + res[4];
		            	$timeout(function(){
		            		var scrollTo_val = $('#inner-content-div1').prop('scrollHeight') + 'px';
		            		$('#inner-content-div1').slimScroll({ scrollTo : scrollTo_val });
		            	})
		           	}
    			}
    		}, function(error){
    			
    		});
    }
	
	$scope.addChat = function(chat){
		var d = new Date(chat.messageTime);
		d.setHours(d.getHours()-10);
		d.setMinutes(d.getMinutes()-30);
		var res = d.toString().split(" ");
		chat.messageTime = res[1] + " " + res[2] + " " + res[4];
		console.log(chat.messageTime);
		$scope.allChats.push(chat);
		$timeout(function(){
    		var scrollTo_val = $('#inner-content-div1').prop('scrollHeight') + 'px';
    		$('#inner-content-div1').slimScroll({ scrollTo : scrollTo_val });
    	},10);
    }
	
	
	$scope.downloadFile = function(cId){
		console.log(cId);
		$('#downloadModal').modal('show');
		$.fileDownload('/downloadFile', {
			httpMethod: "POST",
			data: {id:cId},
			successCallback: function () {
				$('#downloadModal').modal('hide');
            },
            failCallback: function () {
            	$('#downloadModal').modal('hide');
            	$('#errorModal').modal('show');
            }
		});
	}
	
	
	$scope.addToWishlist = function(item){
		console.log(item);
		$http.post('/addToWishlist',{name: item.Name,
			symbol: item.Symbol,
			exchange: item.Exchange}
			).then(function(res){
				notify("Added to wishlist!");
				$scope.getDataResult(res.data.symbol);
			}, function(error){
				notify("Already added in the wishlist.");
				$scope.results = [];
		    	$scope.loadAllWishlist();
			});
	}
	
	$scope.loadAllWishlist = function(){
		$scope.results = [];
		$http.get('/loadAllWishlist',{}
			).then(function(res){
				//console.log(res.data);
				for(var i=0; i<res.data.length; i++){
					$scope.getDataResult(res.data[i].symbol);
				}
			}, function(error){
				
			});
	}
    
	$interval($scope.loadAllWishlist, 30000);
	
	$scope.results = [];
	
    $scope.getDataResult = function(symbol){
    	//console.log("ANGULAR JS"+symbol);
    	new Markit.QuoteService(symbol, function(jsonResult) {
    		$scope.$apply(function(){
    			console.log(jsonResult);
    			jsonResult.MarketCap=jsonResult.MarketCap/1000000;
        		var tmp =moment(jsonResult.MSDate);  
        		jsonResult.MSDate=tmp.format("YYYY-MM-DD");
    			var res1 = jsonResult.Timestamp.split(" ");
    			jsonResult.Timestamp = res1[0] + " " + res1[1] + " " + res1[2] + " " + res1[3] + " " + res1[5];
    			$scope.results.push(jsonResult);
    		});
    		console.log($scope.results);
    	});
    }
    
    $scope.refreshQuote = function(symbol, index){
    	console.log("ANGULAR JS"+symbol);
    	new Markit.QuoteService(symbol, function(jsonResult) {
    		$scope.$apply(function(){
    			jsonResult.MarketCap=jsonResult.MarketCap/1000000;
        		var tmp =moment(jsonResult.MSDate);  
        		jsonResult.MSDate=tmp.format("YYYY-MM-DD");
    			var res1 = jsonResult.Timestamp.split(" ");
    			jsonResult.Timestamp = res1[0] + " " + res1[1] + " " + res1[2] + " " + res1[3] + " " + res1[5];
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
    			notify("Removed from wishlist!");
    			$scope.results.splice(index,1);
    	    	//console.log($scope.results);
    		}, function(error){
    			
    		});
    }
    
    $scope.getQuote = function(item){
    	console.log(item);
    	$scope.item = angular.copy(item);
    	new Markit.QuoteService(item.Symbol, function(jsonResult) {
    		jsonResult.MarketCap=jsonResult.MarketCap/1000000;
    		var tmp =moment(jsonResult.MSDate);  
    		jsonResult.MSDate=tmp.format("YYYY-MM-DD");
    		$scope.$apply(function(){
    			$scope.quoteResult = jsonResult;
    		});
    	});
    
    }
    
    $scope.showQuote = function(index) {
    	console.log($scope.results[index]);
    	$scope.quoteResult = $scope.results[index];
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
        	location.reload();
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

taskApp.controller('ChangePasswordController', function($scope, $http, notify) {
	
	$scope.selectedTab = 2;

	$scope.changePassword = function(){
		console.log($scope.pwd);
		$http.post('/changePassword',{pwd: $scope.pwd}
			).then(function(res){
				notify("Password changed successfully!");
				document.getElementById("pwd").value = '';
				document.getElementById("confPwd").value = '';
			}, function(error){
				
			});
	}
	
});