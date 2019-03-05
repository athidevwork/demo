describe("dti.oasis.filter", function () {
    var records;

    beforeAll(function() {
        records = [
            {"ID": "1000", name: "Iron Man", age: 40},
            {"ID": "2000", name: "Captain America", age: 20},
            {"ID": "3000", name: "Hulk", age: 35}
        ];
    });


    it("TRUE", function () {
        var trueFilter = dti.oasis.filter.TRUE;

        for (var i = 0; i < records.length; i++) {
            expect(trueFilter.filter(records[i])).toBeTruthy();
        }
    });

    it("FALSE", function () {
        var falseFilter = dti.oasis.filter.FALSE;

        for (var i = 0; i < records.length; i++) {
            expect(falseFilter.filter(records[i])).toBeFalsy();
        }
    });

    it("and", function () {
        var trueFilter = dti.oasis.filter.TRUE;
        var falseFilter = dti.oasis.filter.FALSE;

        for (var i = 0; i < records.length; i++) {
            expect(trueFilter.and(trueFilter).filter(records[i])).toBeTruthy();
            expect(trueFilter.and(falseFilter).filter(records[i])).toBeFalsy();

            expect(falseFilter.and(trueFilter).filter(records[i])).toBeFalsy();
            expect(falseFilter.and(falseFilter).filter(records[i])).toBeFalsy();

            expect(dti.oasis.filter.and(trueFilter, trueFilter).filter(records[i])).toBeTruthy();
            expect(dti.oasis.filter.and(trueFilter, falseFilter).filter(records[i])).toBeFalsy();

            expect(dti.oasis.filter.and(falseFilter, trueFilter).filter(records[i])).toBeFalsy();
            expect(dti.oasis.filter.and(falseFilter, falseFilter).filter(records[i])).toBeFalsy();
        }
    });

    it("or", function () {
        var trueFilter = dti.oasis.filter.TRUE;
        var falseFilter = dti.oasis.filter.FALSE;

        for (var i = 0; i < records.length; i++) {
            expect(trueFilter.or(trueFilter).filter(records[i])).toBeTruthy();
            expect(trueFilter.or(falseFilter).filter(records[i])).toBeTruthy();

            expect(falseFilter.or(trueFilter).filter(records[i])).toBeTruthy();
            expect(falseFilter.or(falseFilter).filter(records[i])).toBeFalsy();

            expect(dti.oasis.filter.or(trueFilter, trueFilter).filter(records[i])).toBeTruthy();
            expect(dti.oasis.filter.or(trueFilter, falseFilter).filter(records[i])).toBeTruthy();

            expect(dti.oasis.filter.or(falseFilter, trueFilter).filter(records[i])).toBeTruthy();
            expect(dti.oasis.filter.or(falseFilter, falseFilter).filter(records[i])).toBeFalsy();
        }
    });

    it("not", function () {
        var trueFilter = dti.oasis.filter.TRUE;
        var falseFilter = dti.oasis.filter.FALSE;

        for (var i = 0; i < records.length; i++) {
            expect(trueFilter.not().filter(records[i])).toBeFalsy();
            expect(falseFilter.not().filter(records[i])).toBeTruthy();

            expect(dti.oasis.filter.not(trueFilter).filter(records[i])).toBeFalsy();
            expect(dti.oasis.filter.not(falseFilter).filter(records[i])).toBeTruthy();
        }
    });

    describe("filter by constant", function () {
        it("equal", function() {
            var filter1 = dti.oasis.filter.createFilter({
                values: [{value: "abc"}, {value: "abc"}]
            });

            var filter2 = dti.oasis.filter.createFilter({
                values: [{value: "abc"}, {value: "def"}]
            });

            var filter3 = dti.oasis.filter.createFilter({
                values: [{value: 123}, {value: 123}]
            });

            var filter4 = dti.oasis.filter.createFilter({
                values: [{value: 123}, {value: 456}]
            });

            for (var i = 0; i < records.length; i++) {
                expect(filter1.filter(records[i])).toBeTruthy();
                expect(filter2.filter(records[i])).toBeFalsy();
                expect(filter3.filter(records[i])).toBeTruthy();
                expect(filter4.filter(records[i])).toBeFalsy();
            }
        });

        it("not equal", function() {
            var filter1 = dti.oasis.filter.createFilter({
                condition: "NOT_EQUAL",
                values: [{value: "abc"}, {value: "abc"}]
            });

            var filter2 = dti.oasis.filter.createFilter({
                condition: "NOT_EQUAL",
                values: [{value: "abc"}, {value: "def"}]
            });

            var filter3 = dti.oasis.filter.createFilter({
                condition: "NOT_EQUAL",
                values: [{value: 123}, {value: 123}]
            });

            var filter4 = dti.oasis.filter.createFilter({
                condition: "NOT_EQUAL",
                values: [{value: 123}, {value: 456}]
            });

            for (var i = 0; i < records.length; i++) {
                expect(filter1.filter(records[i])).toBeFalsy();
                expect(filter2.filter(records[i])).toBeTruthy();
                expect(filter3.filter(records[i])).toBeFalsy();
                expect(filter4.filter(records[i])).toBeTruthy();
            }
        });

        it("greater than", function() {
            var filter1 = dti.oasis.filter.createFilter({
                condition: "GREATER_THAN",
                values: [{value: "abc"}, {value: "abc"}]
            });

            var filter2 = dti.oasis.filter.createFilter({
                condition: "GREATER_THAN",
                values: [{value: "abc"}, {value: "aba"}]
            });

            var filter3 = dti.oasis.filter.createFilter({
                condition: "GREATER_THAN",
                values: [{value: "abc"}, {value: "abd"}]
            });

            var filter4 = dti.oasis.filter.createFilter({
                condition: "GREATER_THAN",
                values: [{value: 123}, {value: 123}]
            });

            var filter5 = dti.oasis.filter.createFilter({
                condition: "GREATER_THAN",
                values: [{value: 123}, {value: 121}]
            });

            var filter6 = dti.oasis.filter.createFilter({
                condition: "GREATER_THAN",
                values: [{value: 123}, {value: 124}]
            });

            for (var i = 0; i < records.length; i++) {
                expect(filter1.filter(records[i])).toBeFalsy();
                expect(filter2.filter(records[i])).toBeTruthy();
                expect(filter3.filter(records[i])).toBeFalsy();
                expect(filter4.filter(records[i])).toBeFalsy();
                expect(filter5.filter(records[i])).toBeTruthy();
                expect(filter6.filter(records[i])).toBeFalsy();
            }
        });

        it("greater or equal", function() {
            var filter1 = dti.oasis.filter.createFilter({
                condition: "GREATER_THAN_OR_EQUAL",
                values: [{value: "abc"}, {value: "abc"}]
            });

            var filter2 = dti.oasis.filter.createFilter({
                condition: "GREATER_THAN_OR_EQUAL",
                values: [{value: "abc"}, {value: "aba"}]
            });

            var filter3 = dti.oasis.filter.createFilter({
                condition: "GREATER_THAN_OR_EQUAL",
                values: [{value: "abc"}, {value: "abd"}]
            });

            var filter4 = dti.oasis.filter.createFilter({
                condition: "GREATER_THAN_OR_EQUAL",
                values: [{value: 123}, {value: 123}]
            });

            var filter5 = dti.oasis.filter.createFilter({
                condition: "GREATER_THAN_OR_EQUAL",
                values: [{value: 123}, {value: 121}]
            });

            var filter6 = dti.oasis.filter.createFilter({
                condition: "GREATER_THAN_OR_EQUAL",
                values: [{value: 123}, {value: 124}]
            });

            for (var i = 0; i < records.length; i++) {
                expect(filter1.filter(records[i])).toBeTruthy();
                expect(filter2.filter(records[i])).toBeTruthy();
                expect(filter3.filter(records[i])).toBeFalsy();
                expect(filter4.filter(records[i])).toBeTruthy();
                expect(filter5.filter(records[i])).toBeTruthy();
                expect(filter6.filter(records[i])).toBeFalsy();
            }
        });

        it("less than", function() {
            var filter1 = dti.oasis.filter.createFilter({
                condition: "LESS_THAN",
                values: [{value: "abc"}, {value: "abc"}]
            });

            var filter2 = dti.oasis.filter.createFilter({
                condition: "LESS_THAN",
                values: [{value: "abc"}, {value: "aba"}]
            });

            var filter3 = dti.oasis.filter.createFilter({
                condition: "LESS_THAN",
                values: [{value: "abc"}, {value: "abd"}]
            });

            var filter4 = dti.oasis.filter.createFilter({
                condition: "LESS_THAN",
                values: [{value: 123}, {value: 123}]
            });

            var filter5 = dti.oasis.filter.createFilter({
                condition: "LESS_THAN",
                values: [{value: 123}, {value: 121}]
            });

            var filter6 = dti.oasis.filter.createFilter({
                condition: "LESS_THAN",
                values: [{value: 123}, {value: 124}]
            });

            for (var i = 0; i < records.length; i++) {
                expect(filter1.filter(records[i])).toBeFalsy();
                expect(filter2.filter(records[i])).toBeFalsy();
                expect(filter3.filter(records[i])).toBeTruthy();
                expect(filter4.filter(records[i])).toBeFalsy();
                expect(filter5.filter(records[i])).toBeFalsy();
                expect(filter6.filter(records[i])).toBeTruthy();
            }
        });

        it("less than or equal", function() {
            var filter1 = dti.oasis.filter.createFilter({
                condition: "LESS_THAN_OR_EQUAL",
                values: [{value: "abc"}, {value: "abc"}]
            });

            var filter2 = dti.oasis.filter.createFilter({
                condition: "LESS_THAN_OR_EQUAL",
                values: [{value: "abc"}, {value: "aba"}]
            });

            var filter3 = dti.oasis.filter.createFilter({
                condition: "LESS_THAN_OR_EQUAL",
                values: [{value: "abc"}, {value: "abd"}]
            });

            var filter4 = dti.oasis.filter.createFilter({
                condition: "LESS_THAN_OR_EQUAL",
                values: [{value: 123}, {value: 123}]
            });

            var filter5 = dti.oasis.filter.createFilter({
                condition: "LESS_THAN_OR_EQUAL",
                values: [{value: 123}, {value: 121}]
            });

            var filter6 = dti.oasis.filter.createFilter({
                condition: "LESS_THAN_OR_EQUAL",
                values: [{value: 123}, {value: 124}]
            });

            for (var i = 0; i < records.length; i++) {
                expect(filter1.filter(records[i])).toBeTruthy();
                expect(filter2.filter(records[i])).toBeFalsy();
                expect(filter3.filter(records[i])).toBeTruthy();
                expect(filter4.filter(records[i])).toBeTruthy();
                expect(filter5.filter(records[i])).toBeFalsy();
                expect(filter6.filter(records[i])).toBeTruthy();
            }
        });
    });

    describe("filter by compare", function () {
        it("equal", function() {
            var filter1 = dti.oasis.filter.createFilter({
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "id"},
                    "1000"
                ]
            });
            expect(filter1.filter(records[0])).toBeTruthy();
            expect(filter1.filter(records[1])).toBeFalsy();
            expect(filter1.filter(records[2])).toBeFalsy();

            var filter2 = dti.oasis.filter.createFilter({
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "name"},
                    "Iron Man"
                ]
            });
            expect(filter2.filter(records[0])).toBeTruthy();
            expect(filter2.filter(records[1])).toBeFalsy();
            expect(filter2.filter(records[2])).toBeFalsy();

            var filter3 = dti.oasis.filter.createFilter({
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "age"},
                    20
                ]
            });
            expect(filter3.filter(records[0])).toBeFalsy();
            expect(filter3.filter(records[1])).toBeTruthy();
            expect(filter3.filter(records[2])).toBeFalsy();

            var filter4 = filter1.and(filter2);
            expect(filter4.filter(records[0])).toBeTruthy();
            expect(filter4.filter(records[1])).toBeFalsy();
            expect(filter4.filter(records[2])).toBeFalsy();

            var filter5 = filter1.and(filter3);
            expect(filter5.filter(records[0])).toBeFalsy();
            expect(filter5.filter(records[1])).toBeFalsy();
            expect(filter5.filter(records[2])).toBeFalsy();

            var filter6 = filter1.or(filter3);
            expect(filter6.filter(records[0])).toBeTruthy();
            expect(filter6.filter(records[1])).toBeTruthy();
            expect(filter6.filter(records[2])).toBeFalsy();

            var filter7 = filter1.not();
            expect(filter7.filter(records[0])).toBeFalsy();
            expect(filter7.filter(records[1])).toBeTruthy();
            expect(filter7.filter(records[2])).toBeTruthy();
        });

        it("not equal", function() {
            var filter1 = dti.oasis.filter.createFilter({
                condition: dti.oasis.filter.condition.NOT_EQUAL,
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "id"},
                    "1000"
                ]
            });
            expect(filter1.filter(records[0])).toBeFalsy();
            expect(filter1.filter(records[1])).toBeTruthy();
            expect(filter1.filter(records[2])).toBeTruthy();

            var filter2 = dti.oasis.filter.createFilter({
                condition: dti.oasis.filter.condition.NOT_EQUAL,
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "name"},
                    "Iron Man"
                ]
            });
            expect(filter2.filter(records[0])).toBeFalsy();
            expect(filter2.filter(records[1])).toBeTruthy();
            expect(filter2.filter(records[2])).toBeTruthy();

            var filter3 = dti.oasis.filter.createFilter({
                condition: dti.oasis.filter.condition.NOT_EQUAL,
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "age"},
                    20
                ]
            });
            expect(filter3.filter(records[0])).toBeTruthy();
            expect(filter3.filter(records[1])).toBeFalsy();
            expect(filter3.filter(records[2])).toBeTruthy();

            var filter4 = filter1.and(filter2);
            expect(filter4.filter(records[0])).toBeFalsy();
            expect(filter4.filter(records[1])).toBeTruthy();
            expect(filter4.filter(records[2])).toBeTruthy();

            var filter5 = filter1.and(filter3);
            expect(filter5.filter(records[0])).toBeFalsy();
            expect(filter5.filter(records[1])).toBeFalsy();
            expect(filter5.filter(records[2])).toBeTruthy();

            var filter6 = filter1.or(filter3);
            expect(filter6.filter(records[0])).toBeTruthy();
            expect(filter6.filter(records[1])).toBeTruthy();
            expect(filter6.filter(records[2])).toBeTruthy();

            var filter7 = filter1.not();
            expect(filter7.filter(records[0])).toBeTruthy();
            expect(filter7.filter(records[1])).toBeFalsy();
            expect(filter7.filter(records[2])).toBeFalsy();
        });

        it("greater than", function() {
            var filter1 = dti.oasis.filter.createFilter({
                condition: dti.oasis.filter.condition.GREATER_THAN,
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "id"},
                    "1000"
                ]
            });
            expect(filter1.filter(records[0])).toBeFalsy();
            expect(filter1.filter(records[1])).toBeTruthy();
            expect(filter1.filter(records[2])).toBeTruthy();

            var filter2 = dti.oasis.filter.createFilter({
                condition: dti.oasis.filter.condition.GREATER_THAN,
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "age"},
                    30
                ]
            });
            expect(filter2.filter(records[0])).toBeTruthy();
            expect(filter2.filter(records[1])).toBeFalsy();
            expect(filter2.filter(records[2])).toBeTruthy();

            var filter3 = filter1.and(filter2);
            expect(filter3.filter(records[0])).toBeFalsy();
            expect(filter3.filter(records[1])).toBeFalsy();
            expect(filter3.filter(records[2])).toBeTruthy();

            var filter4 = filter1.or(filter2);
            expect(filter4.filter(records[0])).toBeTruthy();
            expect(filter4.filter(records[1])).toBeTruthy();
            expect(filter4.filter(records[2])).toBeTruthy();

            var filter5 = filter1.not();
            expect(filter5.filter(records[0])).toBeTruthy();
            expect(filter5.filter(records[1])).toBeFalsy();
            expect(filter5.filter(records[2])).toBeFalsy();
        });

        it("greater than or equal", function() {
            var filter1 = dti.oasis.filter.createFilter({
                condition: dti.oasis.filter.condition.GREATER_THAN_OR_EQUAL,
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "id"},
                    "3000"
                ]
            });
            expect(filter1.filter(records[0])).toBeFalsy();
            expect(filter1.filter(records[1])).toBeFalsy();
            expect(filter1.filter(records[2])).toBeTruthy();

            var filter2 = dti.oasis.filter.createFilter({
                condition: dti.oasis.filter.condition.GREATER_THAN_OR_EQUAL,
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "age"},
                    40
                ]
            });
            expect(filter2.filter(records[0])).toBeTruthy();
            expect(filter2.filter(records[1])).toBeFalsy();
            expect(filter2.filter(records[2])).toBeFalsy();

            var filter3 = filter1.and(filter2);
            expect(filter3.filter(records[0])).toBeFalsy();
            expect(filter3.filter(records[1])).toBeFalsy();
            expect(filter3.filter(records[2])).toBeFalsy();

            var filter4 = filter1.or(filter2);
            expect(filter4.filter(records[0])).toBeTruthy();
            expect(filter4.filter(records[1])).toBeFalsy();
            expect(filter4.filter(records[2])).toBeTruthy();

            var filter5 = filter1.not();
            expect(filter5.filter(records[0])).toBeTruthy();
            expect(filter5.filter(records[1])).toBeTruthy();
            expect(filter5.filter(records[2])).toBeFalsy();
        });

        it("less than", function() {
            var filter1 = dti.oasis.filter.createFilter({
                condition: dti.oasis.filter.condition.LESS_THAN,
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "id"},
                    "2000"
                ]
            });
            expect(filter1.filter(records[0])).toBeTruthy();
            expect(filter1.filter(records[1])).toBeFalsy();
            expect(filter1.filter(records[2])).toBeFalsy();

            var filter2 = dti.oasis.filter.createFilter({
                condition: dti.oasis.filter.condition.LESS_THAN,
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "age"},
                    30
                ]
            });
            expect(filter2.filter(records[0])).toBeFalsy();
            expect(filter2.filter(records[1])).toBeTruthy();
            expect(filter2.filter(records[2])).toBeFalsy();

            var filter3 = filter1.and(filter2);
            expect(filter3.filter(records[0])).toBeFalsy();
            expect(filter3.filter(records[1])).toBeFalsy();
            expect(filter3.filter(records[2])).toBeFalsy();

            var filter4 = filter1.or(filter2);
            expect(filter4.filter(records[0])).toBeTruthy();
            expect(filter4.filter(records[1])).toBeTruthy();
            expect(filter4.filter(records[2])).toBeFalsy();

            var filter5 = filter1.not();
            expect(filter5.filter(records[0])).toBeFalsy();
            expect(filter5.filter(records[1])).toBeTruthy();
            expect(filter5.filter(records[2])).toBeTruthy();
        });

        it("less than or equal", function() {
            var filter1 = dti.oasis.filter.createFilter({
                condition: dti.oasis.filter.condition.LESS_THAN_OR_EQUAL,
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "id"},
                    "2000"
                ]
            });
            expect(filter1.filter(records[0])).toBeTruthy();
            expect(filter1.filter(records[1])).toBeTruthy();
            expect(filter1.filter(records[2])).toBeFalsy();

            var filter2 = dti.oasis.filter.createFilter({
                condition: dti.oasis.filter.condition.LESS_THAN_OR_EQUAL,
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "age"},
                    35
                ]
            });
            expect(filter2.filter(records[0])).toBeFalsy();
            expect(filter2.filter(records[1])).toBeTruthy();
            expect(filter2.filter(records[2])).toBeTruthy();

            var filter3 = filter1.and(filter2);
            expect(filter3.filter(records[0])).toBeFalsy();
            expect(filter3.filter(records[1])).toBeTruthy();
            expect(filter3.filter(records[2])).toBeFalsy();

            var filter4 = filter1.or(filter2);
            expect(filter4.filter(records[0])).toBeTruthy();
            expect(filter4.filter(records[1])).toBeTruthy();
            expect(filter4.filter(records[2])).toBeTruthy();

            var filter5 = filter1.not();
            expect(filter5.filter(records[0])).toBeFalsy();
            expect(filter5.filter(records[1])).toBeFalsy();
            expect(filter5.filter(records[2])).toBeTruthy();
        });
    });

    describe("filter by function", function () {
        it("contains", function () {
            var filter1 = dti.oasis.filter.createFilter({
                type: dti.oasis.filter.type.FUNCTION,
                condition: dti.oasis.filter.filterFunction.CONTAINS,
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "name"},
                    "Iron"
                ]
            });
            expect(filter1.filter(records[0])).toBeTruthy();
            expect(filter1.filter(records[1])).toBeFalsy();
            expect(filter1.filter(records[2])).toBeFalsy();

            var filter2 = dti.oasis.filter.createFilter({
                type: dti.oasis.filter.type.FUNCTION,
                condition: dti.oasis.filter.filterFunction.CONTAINS,
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "name"},
                    "America"
                ]
            });
            expect(filter2.filter(records[0])).toBeFalsy();
            expect(filter2.filter(records[1])).toBeTruthy();
            expect(filter2.filter(records[2])).toBeFalsy();

            var filter3 = filter1.and(filter2);
            expect(filter3.filter(records[0])).toBeFalsy();
            expect(filter3.filter(records[1])).toBeFalsy();
            expect(filter3.filter(records[2])).toBeFalsy();

            var filter4 = filter1.or(filter2);
            expect(filter4.filter(records[0])).toBeTruthy();
            expect(filter4.filter(records[1])).toBeTruthy();
            expect(filter4.filter(records[2])).toBeFalsy();

            var filter5 = filter1.not();
            expect(filter5.filter(records[0])).toBeFalsy();
            expect(filter5.filter(records[1])).toBeTruthy();
            expect(filter5.filter(records[2])).toBeTruthy();
        });

        it("starts-with", function () {
            var filter1 = dti.oasis.filter.createFilter({
                type: dti.oasis.filter.type.FUNCTION,
                condition: dti.oasis.filter.filterFunction.STARTS_WITH,
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "name"},
                    "Iron"
                ]
            });
            expect(filter1.filter(records[0])).toBeTruthy();
            expect(filter1.filter(records[1])).toBeFalsy();
            expect(filter1.filter(records[2])).toBeFalsy();

            var filter2 = dti.oasis.filter.createFilter({
                type: dti.oasis.filter.type.FUNCTION,
                condition: dti.oasis.filter.filterFunction.STARTS_WITH,
                values: [
                    {type: dti.oasis.filter.valueType.COLUMN, value: "name"},
                    "Man"
                ]
            });
            expect(filter2.filter(records[0])).toBeFalsy();
            expect(filter2.filter(records[1])).toBeFalsy();
            expect(filter2.filter(records[2])).toBeFalsy();
        });
    });

    describe("filter by value function", function () {
        it("uppercase", function() {
            var filter1 = dti.oasis.filter.createFilter({
                type: dti.oasis.filter.type.FUNCTION,
                condition: dti.oasis.filter.filterFunction.CONTAINS,
                values: [
                    {
                        type: dti.oasis.filter.valueType.FUNCTION,
                        functionName: dti.oasis.filter.valueFunction.UPPERCASE,
                        value: [{
                            type: dti.oasis.filter.valueType.COLUMN,
                            value: "name"
                        }]
                    },
                    "IRON"
                ]
            });

            expect(filter1.filter(records[0])).toBeTruthy();
            expect(filter1.filter(records[1])).toBeFalsy();
            expect(filter1.filter(records[2])).toBeFalsy();
        });

        it("lowercase", function() {
            var filter1 = dti.oasis.filter.createFilter({
                type: dti.oasis.filter.type.FUNCTION,
                condition: dti.oasis.filter.filterFunction.CONTAINS,
                values: [
                    {
                        type: dti.oasis.filter.valueType.FUNCTION,
                        functionName: dti.oasis.filter.valueFunction.LOWERCASE,
                        value: [{
                            type: dti.oasis.filter.valueType.COLUMN,
                            value: "name"
                        }]
                    },
                    "iron"
                ]
            });

            expect(filter1.filter(records[0])).toBeTruthy();
            expect(filter1.filter(records[1])).toBeFalsy();
            expect(filter1.filter(records[2])).toBeFalsy();
        });

        it("number", function() {
            var filter1 = dti.oasis.filter.createFilter({
                condition: dti.oasis.filter.condition.GREATER_THAN,
                values: [
                    {
                        type: dti.oasis.filter.valueType.FUNCTION,
                        functionName: dti.oasis.filter.valueFunction.NUMBER,
                        value: [{
                            type: dti.oasis.filter.valueType.COLUMN,
                            value: "id"
                        }]
                    },
                    90
                ]
            });

            expect(filter1.filter(records[0])).toBeTruthy();
            expect(filter1.filter(records[1])).toBeTruthy();
            expect(filter1.filter(records[2])).toBeTruthy();


            var filter2 = dti.oasis.filter.createFilter({
                condition: dti.oasis.filter.condition.GREATER_THAN,
                values: [
                    {
                        type: dti.oasis.filter.valueType.COLUMN,
                        value: "id"
                    },
                    "90"
                ]
            });
            expect(filter2.filter(records[0])).toBeFalsy();
            expect(filter2.filter(records[1])).toBeFalsy();
            expect(filter2.filter(records[2])).toBeFalsy();
        });

        it("concat", function() {
            var filter1 = dti.oasis.filter.createFilter({
                values: [
                    {
                        type: dti.oasis.filter.valueType.FUNCTION,
                        functionName: dti.oasis.filter.valueFunction.CONCAT,
                        value: [
                            {
                                type: dti.oasis.filter.valueType.COLUMN,
                                value: "id"
                            },
                            {value: ", "},
                            {
                                type: dti.oasis.filter.valueType.COLUMN,
                                value: "name"
                            },
                            {value: ", "},
                            {
                                type: dti.oasis.filter.valueType.COLUMN,
                                value: "age"
                            }
                        ]
                    },
                    "1000, Iron Man, 40"
                ]
            });

            expect(filter1.filter(records[0])).toBeTruthy();
            expect(filter1.filter(records[1])).toBeFalsy();
            expect(filter1.filter(records[2])).toBeFalsy();
        });

        it("substring", function() {
            var filter1 = dti.oasis.filter.createFilter({
                values: [
                    {
                        type: dti.oasis.filter.valueType.FUNCTION,
                        functionName: dti.oasis.filter.valueFunction.SUBSTRING,
                        value: [
                            {
                                type: dti.oasis.filter.valueType.COLUMN,
                                value: "name"
                            },
                            9
                        ]
                    },
                    "America"
                ]
            });

            expect(filter1.filter(records[0])).toBeFalsy();
            expect(filter1.filter(records[1])).toBeTruthy();
            expect(filter1.filter(records[2])).toBeFalsy();

            var filter2 = dti.oasis.filter.createFilter({
                values: [
                    {
                        type: dti.oasis.filter.valueType.FUNCTION,
                        functionName: dti.oasis.filter.valueFunction.SUBSTRING,
                        value: [
                            {
                                type: dti.oasis.filter.valueType.COLUMN,
                                value: "name"
                            },
                            6,
                            2
                        ]
                    },
                    "Ma"
                ]
            });

            expect(filter2.filter(records[0])).toBeTruthy();
            expect(filter2.filter(records[1])).toBeFalsy();
            expect(filter2.filter(records[2])).toBeFalsy();
        });

        it("translate", function() {
            var filter1 = dti.oasis.filter.createFilter({
                values: [
                    {
                        type: dti.oasis.filter.valueType.FUNCTION,
                        functionName: dti.oasis.filter.valueFunction.TRANSLATE,
                        value: [
                            {
                                type: dti.oasis.filter.valueType.COLUMN,
                                value: "name"
                            },
                            "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                            "abcdefghijklmnopqrstuvwxyz"
                        ]
                    },
                    "hulk"
                ]
            });

            expect(filter1.filter(records[0])).toBeFalsy();
            expect(filter1.filter(records[1])).toBeFalsy();
            expect(filter1.filter(records[2])).toBeTruthy();

            var filter2 = dti.oasis.filter.createFilter({
                values: [
                    {
                        type: dti.oasis.filter.valueType.FUNCTION,
                        functionName: dti.oasis.filter.valueFunction.TRANSLATE,
                        value: [
                            {
                                type: dti.oasis.filter.valueType.COLUMN,
                                value: "name"
                            },
                            "abcdefghijklmnopqrstuvwxyz",
                            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        ]
                    },
                    "HULK"
                ]
            });

            expect(filter2.filter(records[0])).toBeFalsy();
            expect(filter2.filter(records[1])).toBeFalsy();
            expect(filter2.filter(records[2])).toBeTruthy();

            var filter3 = dti.oasis.filter.createFilter({
                values: [
                    {
                        type: dti.oasis.filter.valueType.FUNCTION,
                        functionName: dti.oasis.filter.valueFunction.TRANSLATE,
                        value: [
                            {
                                type: dti.oasis.filter.valueType.COLUMN,
                                value: "name"
                            },
                            "Man",
                            "Maaan"
                        ]
                    },
                    "Iron Maaan"
                ]
            });
            expect(filter3.filter(records[0])).toBeTruthy();
            expect(filter3.filter(records[1])).toBeFalsy();
            expect(filter3.filter(records[2])).toBeFalsy();
        });
    });

    it("filter by custom function", function () {
        var filter = dti.oasis.filter.createFilter({
            type: dti.oasis.filter.type.CUSTOM_FUNCTION,
            condition: function (record) {
                return (record["name"] == "Hulk");
            }
        });

        expect(filter.filter(records[0])).toBeFalsy();
        expect(filter.filter(records[1])).toBeFalsy();
        expect(filter.filter(records[2])).toBeTruthy();
    });

    describe("filter operator precedence", function () {
        it ("and > or", function() {
            var filter = dti.oasis.filter.compile("ID = '1000' and name = 'Spider Man' or name = 'Hulk' or name = 'Captain America'");
            expect(filter.filter(records[0])).toBeFalsy();
            expect(filter.filter(records[1])).toBeTruthy();
            expect(filter.filter(records[2])).toBeTruthy();
        });

        it ("parenthese > and  > or", function() {
            var filter = dti.oasis.filter.compile("ID = '1000' and (name = 'Spider Man' or name = 'Iron Man') or name = 'Captain America'");
            expect(filter.filter(records[0])).toBeTruthy();
            expect(filter.filter(records[1])).toBeTruthy();
            expect(filter.filter(records[2])).toBeFalsy();
        });

        it ("and or and ", function() {
            var filter = dti.oasis.filter.compile("ID = '1000' and name = 'Spider Man' or name = 'Hulk' or name = 'Captain America' and ID = '2000'");
            expect(filter.filter(records[0])).toBeFalsy();
            expect(filter.filter(records[1])).toBeTruthy();
            expect(filter.filter(records[2])).toBeTruthy();
        });

        it ("nested parentheses", function() {
            var filter = dti.oasis.filter.compile("ID = '1000' and name = 'Captain America' or (name = 'Spider Man' or (age < 40 and ID = '3000'))");
            expect(filter.filter(records[0])).toBeFalsy();
            expect(filter.filter(records[1])).toBeFalsy();
            expect(filter.filter(records[2])).toBeTruthy();
        });

        describe("filter by value function", function () {
            it ("contains, tranlsate", function() {
                var filter = dti.oasis.filter.compile("ID = '1000' and (name = 'Spider Man' or name = 'Iron Man') or contains(translate(name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'hulk')");
                expect(filter.filter(records[0])).toBeTruthy();
                expect(filter.filter(records[1])).toBeFalsy();
                expect(filter.filter(records[2])).toBeTruthy();
            });

            it ("uppercase, concat, substring", function() {
                var filter = dti.oasis.filter.compile("ID = '1000' and (name = 'Spider Man' or name = 'Iron Man') or uppercase(concat(substring(name, 9, 2), ' test')) = 'AM TEST'");
                expect(filter.filter(records[0])).toBeTruthy();
                expect(filter.filter(records[1])).toBeTruthy();
                expect(filter.filter(records[2])).toBeFalsy();
            });
        });
    });

    describe("filter by filter string", function () {
        it ("=", function () {
            var filter = dti.oasis.filter.compile("name = 'Hulk'");

            expect(filter.filter(records[0])).toBeFalsy();
            expect(filter.filter(records[1])).toBeFalsy();
            expect(filter.filter(records[2])).toBeTruthy();

            filter = dti.oasis.filter.compile("name = \"Hulk\"");

            expect(filter.filter(records[0])).toBeFalsy();
            expect(filter.filter(records[1])).toBeFalsy();
            expect(filter.filter(records[2])).toBeTruthy();
        });

        it ("!=", function () {
            var filter = dti.oasis.filter.compile("name != 'Hulk'");

            expect(filter.filter(records[0])).toBeTruthy();
            expect(filter.filter(records[1])).toBeTruthy();
            expect(filter.filter(records[2])).toBeFalsy();
        });

        it (">", function () {
            var filter = dti.oasis.filter.compile("age > 35");

            expect(filter.filter(records[0])).toBeTruthy();
            expect(filter.filter(records[1])).toBeFalsy();
            expect(filter.filter(records[2])).toBeFalsy();
        });

        it (">=", function () {
            var filter = dti.oasis.filter.compile("age >= 35");

            expect(filter.filter(records[0])).toBeTruthy();
            expect(filter.filter(records[1])).toBeFalsy();
            expect(filter.filter(records[2])).toBeTruthy();
        });

        it ("<", function () {
            var filter = dti.oasis.filter.compile("age < 35");

            expect(filter.filter(records[0])).toBeFalsy();
            expect(filter.filter(records[1])).toBeTruthy();
            expect(filter.filter(records[2])).toBeFalsy();
        });

        it ("<=", function () {
            var filter = dti.oasis.filter.compile("age <= 35");

            expect(filter.filter(records[0])).toBeFalsy();
            expect(filter.filter(records[1])).toBeTruthy();
            expect(filter.filter(records[2])).toBeTruthy();
        });

        it ("contains", function() {
            var filter = dti.oasis.filter.compile("contains(name, 'Man')");

            expect(filter.filter(records[0])).toBeTruthy();
            expect(filter.filter(records[1])).toBeFalsy();
            expect(filter.filter(records[2])).toBeFalsy();
        });

        it ("starts-with", function() {
            var filter = dti.oasis.filter.compile("starts-with(name, 'Iron')");

            expect(filter.filter(records[0])).toBeTruthy();
            expect(filter.filter(records[1])).toBeFalsy();
            expect(filter.filter(records[2])).toBeFalsy();

            filter = dti.oasis.filter.compile("starts-with(translate(name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'captain')");

            expect(filter.filter(records[0])).toBeFalsy();
            expect(filter.filter(records[1])).toBeTruthy();
            expect(filter.filter(records[2])).toBeFalsy();
        });

        it ("and", function() {
            var filter = dti.oasis.filter.compile("ID = '1000' and name = 'Iron Man'");
            expect(filter.filter(records[0])).toBeTruthy();
            expect(filter.filter(records[1])).toBeFalsy();
            expect(filter.filter(records[2])).toBeFalsy();
        });

        it ("or", function() {
            var filter = dti.oasis.filter.compile("name = 'Iron Man' or name = 'Hulk'");
            expect(filter.filter(records[0])).toBeTruthy();
            expect(filter.filter(records[1])).toBeFalsy();
            expect(filter.filter(records[2])).toBeTruthy();
        });

        it ("not", function() {
            var filter = dti.oasis.filter.compile("not(name = 'Iron Man')");
            expect(filter.filter(records[0])).toBeFalsy();
            expect(filter.filter(records[1])).toBeTruthy();
            expect(filter.filter(records[2])).toBeTruthy();

            filter = dti.oasis.filter.compile("not(name = 'Iron Man' or name = 'Hulk')");
            expect(filter.filter(records[0])).toBeFalsy();
            expect(filter.filter(records[1])).toBeTruthy();
            expect(filter.filter(records[2])).toBeFalsy();
        });

        it ("()", function() {
            var filter = dti.oasis.filter.compile("(name = 'Iron Man' or name = 'Hulk') and (age < 40)");
            expect(filter.filter(records[0])).toBeFalsy();
            expect(filter.filter(records[1])).toBeFalsy();
            expect(filter.filter(records[2])).toBeTruthy();
        });

        describe("filter by value function", function () {
            it ("uppercase", function() {
                var filter = dti.oasis.filter.compile("uppercase(name) = 'HULK'");

                expect(filter.filter(records[0])).toBeFalsy();
                expect(filter.filter(records[1])).toBeFalsy();
                expect(filter.filter(records[2])).toBeTruthy();
            });

            it ("lowercase", function() {
                var filter = dti.oasis.filter.compile("lowercase(name) = 'hulk'");

                expect(filter.filter(records[0])).toBeFalsy();
                expect(filter.filter(records[1])).toBeFalsy();
                expect(filter.filter(records[2])).toBeTruthy();
            });

            it ("number", function() {
                var filter = dti.oasis.filter.compile("number(ID) > 1000");
                expect(filter.filter(records[0])).toBeFalsy();
                expect(filter.filter(records[1])).toBeTruthy();
                expect(filter.filter(records[2])).toBeTruthy();

                filter = dti.oasis.filter.compile("number(ID) < 80");
                expect(filter.filter(records[0])).toBeFalsy();
                expect(filter.filter(records[1])).toBeFalsy();
                expect(filter.filter(records[2])).toBeFalsy();
            });

            it ("concat", function() {
                var filter = dti.oasis.filter.compile("concat(ID, ', ', name, ', ', age) = '1000, Iron Man, 40'");

                expect(filter.filter(records[0])).toBeTruthy();
                expect(filter.filter(records[1])).toBeFalsy();
                expect(filter.filter(records[2])).toBeFalsy();

                filter = dti.oasis.filter.compile("concat(ID, ', ', uppercase(name), ', ', age) = '3000, HULK, 35'");

                expect(filter.filter(records[0])).toBeFalsy();
                expect(filter.filter(records[1])).toBeFalsy();
                expect(filter.filter(records[2])).toBeTruthy();

                var filter = dti.oasis.filter.compile("concat(age1, '41') > 35");
                expect(filter.filter(records[0])).toBeTruthy();
                expect(filter.filter(records[1])).toBeTruthy();
                expect(filter.filter(records[2])).toBeTruthy();
            });

            it ("substring", function() {
                var filter = dti.oasis.filter.compile("substring(name, 9, 2) = 'Am'");
                expect(filter.filter(records[0])).toBeFalsy();
                expect(filter.filter(records[1])).toBeTruthy();
                expect(filter.filter(records[2])).toBeFalsy();

                filter = dti.oasis.filter.compile("substring(name, 6) = 'Man'");
                expect(filter.filter(records[0])).toBeTruthy();
                expect(filter.filter(records[1])).toBeFalsy();
                expect(filter.filter(records[2])).toBeFalsy();
            });

            it ("translate", function() {
                var filter = dti.oasis.filter.compile("translate(name, 'Man', 'Maan') = 'Iron Maan'");
                expect(filter.filter(records[0])).toBeTruthy();
                expect(filter.filter(records[1])).toBeFalsy();
                expect(filter.filter(records[2])).toBeFalsy();

                filter = dti.oasis.filter.compile("translate(name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'iron man'");
                expect(filter.filter(records[0])).toBeTruthy();
                expect(filter.filter(records[1])).toBeFalsy();
                expect(filter.filter(records[2])).toBeFalsy();

                filter = dti.oasis.filter.compile("translate(name, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ') = 'IRON MAN'");
                expect(filter.filter(records[0])).toBeTruthy();
                expect(filter.filter(records[1])).toBeFalsy();
                expect(filter.filter(records[2])).toBeFalsy();
            });
        });

        xit("test performance", function () {
            function testCompilePerformance(filterString) {
                var beginTime = new Date();
                dti.oasis.filter.compile(filterString);
                var endTime = new Date();
                console.log("Compile \"" + filterString + "\"" + " takes:\n");
                console.log((endTime.getTime() - beginTime.getTime())/1000 + "s");
            }

            testCompilePerformance("//ROW[(DISPLAY_IND = 'Y' and UPDATE_IND != 'D')]");
            testCompilePerformance("//ROW[(DISPLAY_IND = 'Y' and UPDATE_IND != 'D')]");
            testCompilePerformance("//ROW[(DISPLAY_IND = 'Y' and UPDATE_IND != 'D' and (CCLAIMNO = 'OPEN' or CCLAIMNO = 'PENDING' or CCLAIMNO = 'REOPEN'))]");
            testCompilePerformance("//ROW[(DISPLAY_IND = 'Y' and UPDATE_IND != 'D' and (CCLAIMNO = 'OPEN' or CCLAIMNO = 'PENDING' or CCLAIMNO = 'REOPEN') and (substring(CREPORTDATE, 6) = '2016'))]");
            testCompilePerformance("//ROW[(DISPLAY_IND = 'Y' and UPDATE_IND != 'D' and (CCLAIMNO = 'OPEN' or CCLAIMNO = 'PENDING' or CCLAIMNO = 'REOPEN') and number(concat(substring(CCOVERAGEEXPFROM,7,4),substring(CCOVERAGEEXPFROM,1,2),substring(CCOVERAGEEXPFROM,4,2))) > 20160101)]");

            expect(true).toBeTruthy();
        });
    });
});