taskApp.config(function ($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: '/jsp/dashboard',
            controller: 'DashboardController'
        })
        .when('/myAccount', {
            templateUrl: '/assets/app/views/myAccount.html',
            controller: 'MyAccountController'
        })
        .when('/changePass', {
            templateUrl: '/assets/app/views/changePass.html',
            controller: 'ChangePasswordController'
        })
        .when('/manageUsers', {
            templateUrl: '/assets/app/views/manageUsers.html',
            controller: 'UserController'
        })

});
