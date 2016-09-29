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
    var mod = ng.module('departmentModule', ['ngCrud', 'ui.router']);

    mod.constant('departmentModel', {
        name: 'department',
        displayName: 'Department',
		url: 'departments',
        fields: {            name: {
                displayName: 'Name',
                type: 'String',
                required: true
            }        }
    });

    mod.config(['$stateProvider',
        function($sp){
            var basePath = 'src/modules/department/';
            var baseInstancePath = basePath + 'instance/';

            $sp.state('department', {
                url: '/departments?page&limit',
                abstract: true,
                parent: 'companyInstance',
                views: {
                     companyInstanceView: {
                        templateUrl: basePath + 'department.tpl.html',
                        controller: 'departmentCtrl'
                    }
                },
                resolve: {
                    model: 'departmentModel',
                    departments: ['company', '$stateParams', function (company, $params) {
                            return company.getList('departments', $params);
                        }]
                }
            });
            $sp.state('departmentList', {
                url: '/list',
                parent: 'department',
                views: {
                    departmentView: {
                        templateUrl: basePath + 'list/department.list.tpl.html',
                        controller: 'departmentListCtrl',
                        controllerAs: 'ctrl'
                    }
                }
            });
            $sp.state('departmentNew', {
                url: '/new',
                parent: 'department',
                views: {
                    departmentView: {
                        templateUrl: basePath + 'new/department.new.tpl.html',
                        controller: 'departmentNewCtrl',
                        controllerAs: 'ctrl'
                    }
                }
            });
            $sp.state('departmentInstance', {
                url: '/{departmentId:int}',
                abstract: true,
                parent: 'department',
                views: {
                    departmentView: {
                        template: '<div ui-view="departmentInstanceView"></div>'
                    }
                },
                resolve: {
                    department: ['departments', '$stateParams', function (departments, $params) {
                            return departments.get($params.departmentId);
                        }]
                }
            });
            $sp.state('departmentDetail', {
                url: '/details',
                parent: 'departmentInstance',
                views: {
                    departmentInstanceView: {
                        templateUrl: baseInstancePath + 'detail/department.detail.tpl.html',
                        controller: 'departmentDetailCtrl'
                    }
                }
            });
            $sp.state('departmentEdit', {
                url: '/edit',
                sticky: true,
                parent: 'departmentInstance',
                views: {
                    departmentInstanceView: {
                        templateUrl: baseInstancePath + 'edit/department.edit.tpl.html',
                        controller: 'departmentEditCtrl',
                        controllerAs: 'ctrl'
                    }
                }
            });
            $sp.state('departmentDelete', {
                url: '/delete',
                parent: 'departmentInstance',
                views: {
                    departmentInstanceView: {
                        templateUrl: baseInstancePath + 'delete/department.delete.tpl.html',
                        controller: 'departmentDeleteCtrl',
                        controllerAs: 'ctrl'
                    }
                }
            });
            $sp.state('departmentEmployees', {
                url: '/employees',
                parent: 'departmentInstance',
                abstract: true,
                views: {
                    departmentInstanceView: {
                        template: '<div ui-view="departmentEmployeesView"></div>'
                    }
                },
                resolve: {
                    employees: ['department', function (department) {
                            return department.getList('employees');
                        }],
                    model: 'employeeModel'
                }
            });
            $sp.state('departmentEmployeesList', {
                url: '/list',
                parent: 'departmentEmployees',
                views: {
                    departmentEmployeesView: {
                        templateUrl: baseInstancePath + 'employees/list/department.employees.list.tpl.html',
                        controller: 'departmentEmployeesListCtrl'
                    }
                }
            });
            $sp.state('departmentEmployeesEdit', {
                url: '/edit',
                parent: 'departmentEmployees',
                views: {
                    departmentEmployeesView: {
                        templateUrl: baseInstancePath + 'employees/edit/department.employees.edit.tpl.html',
                        controller: 'departmentEmployeesEditCtrl',
                        controllerAs: 'ctrl'
                    }
                },
                resolve: {
                    pool: ['Restangular', 'model', function (r, model) {
                            return r.all(model.url).getList();
                        }]
                }
            });
	}]);
})(window.angular);
