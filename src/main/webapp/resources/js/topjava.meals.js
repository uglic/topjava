// $(document).ready(function () {
$(function () {
    makeEditable({
            ajaxUrl: "ajax/meals/",
            datatableApi: $("#datatable").DataTable({
                "paging": false,
                "info": true,
                "columns": [
                    {
                        "data": "dateTime"
                    },
                    {
                        "data": "description"
                    },
                    {
                        "data": "calories"
                    },
                    {
                        "defaultContent": "Edit",
                        "orderable": false
                    },
                    {
                        "defaultContent": "Delete",
                        "orderable": false
                    }
                ],
                "order": [
                    [
                        0,
                        "desc"
                    ]
                ]
            }),
            onAfterDeleteCallback: applyMealsFilter,
            onAfterSaveCallback: applyMealsFilter
        }
    );
    initMealsFilter();
});

let filterMeals;

function initMealsFilter() { // global namespace so -meals-
    filterMeals = $("#filterForm");
    filterMeals.submit((e) => e.preventDefault());
    filterMeals.children("button[type='submit'], button[type='reset']").click(function (e) {
            if ($(this).attr("type") === "reset") $(this).closest("form")[0].reset();
            applyFilter();
        }
    );
}

function applyMealsFilter() {
    $.get(context.ajaxUrl + "filter?" + filterMeals.serialize(), updateData);
}
