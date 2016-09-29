/*
The MIT License (MIT)

Copyright (c) 2015 Los Andes University

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
(function (ng) {
    var mod = ng.module('companyModule', ['ngCrud', 'ui.router']);

    mod.constant('companyModel', {
        name: 'company',
        displayName: 'Company',
		url: 'companys',
        fields: {            name: {
                displayName: 'Name',
                type: 'String',
                required: true
            }        }
    });

    mod.config(['$stateProvider',
        function($sp){
            var basePath = 'src/modules/company/';
            var baseInstancePath = basePath + 'instance/';

            $sp.state('company', {
                url: '/companys?page&limit',
                abstract: true,
                
                views: {
                     mainView: {
                        templateUrl: basePath + 'company.tpl.html',
                        controller: 'companyCtrl'
                    }
                },
                resolve: {
                    model: 'companyModel',
                    companys: ['Restangular', 'model', '$stateParams', function (r, model, $params) {
                            return r.all(model.url).getList($params);
                        }]
                }
            });
            $sp.state('companyList', {
                url: '/list',
                parent: 'company',
                views: {
                    companyView: {
                        templateUrl: basePath + 'list/company.list.tpl.html',
                        controller: 'companyListCtrl',
                        controllerAs: 'ctrl'
                    }
                }
            });
            $sp.state('companyNew', {
                url: '/new',
                parent: 'company',
                views: {
                    companyView: {
                        templateUrl: basePath + 'new/company.new.tpl.html',
                        controller: 'companyNewCtrl',
                        controllerAs: 'ctrl'
                    }
                }
            });
            $sp.state('companyInstance', {
                url: '/{companyId:int}',
                abstract: true,
                parent: 'company',
                views: {
                    companyView: {
                        template: '<div ui-view="companyInstanceView"></div>'
                    }
                },
                resolve: {
                    company: ['companys', '$stateParams', function (companys, $params) {
                            return companys.get($params.companyId);
                        }]
                }
            });
            $sp.state('companyDetail', {
                url: '/details',
                parent: 'companyInstance',
                views: {
                    companyInstanceView: {
                        templateUrl: baseInstancePath + 'detail/company.detail.tpl.html',
                        controller: 'companyDetailCtrl'
                    }
                }
            });
            $sp.state('companyEdit', {
                url: '/edit',
                sticky: true,
                parent: 'companyInstance',
                views: {
                    companyInstanceView: {
                        templateUrl: baseInstancePath + 'edit/company.edit.tpl.html',
                        controller: 'companyEditCtrl',
                        controllerAs: 'ctrl'
                    }
                }
            });
            $sp.state('companyDelete', {
                url: '/delete',
                parent: 'companyInstance',
                views: {
                    companyInstanceView: {
                        templateUrl: baseInstancePath + 'delete/company.delete.tpl.html',
                        controller: 'companyDeleteCtrl',
                        controllerAs: 'ctrl'
                    }
                }
            });
	}]);
})(window.angular);
