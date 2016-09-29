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
describe('Company E2E Testing', function () {

	var nameVarTest = 'Val' + Math.floor(Math.random() * 10000);

    beforeEach(function () {
        browser.addMockModule('ngCrudMock', function () {
            var mod = angular.module('ngCrudMock');

            mod.run(['ngCrudMock.mockRecords', function(records){
                records['companys'] = [];

            }]);
        });
    });

    it('should create one company', function () {
        browser.get('#/company');
        element(by.id('create-company')).click();
        element(by.id('name')).sendKeys(nameVarTest);
        element(by.id('save-company')).click();
        expect(element.all(by.repeater('record in records')).count()).toEqual(1);
    });

    it('should read one company', function () {
        expect(element(by.id('0-name')).getText()).toBe(nameVarTest);
    });

    it('should edit one company', function () {
        element(by.id('0-edit-btn')).click();

        element(by.id('name')).clear().sendKeys('New' + nameVarTest);

        element(by.id('save-company')).click();

        expect(element(by.id('0-name')).getText()).toBe('New' + nameVarTest);
    });

    it('should delete the company', function () {
        element(by.id('0-delete-btn')).click();
        expect(element.all(by.id('0-name')).count()).toEqual(0);
    });
});
