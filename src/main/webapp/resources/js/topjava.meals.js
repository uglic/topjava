// $(document).ready(function () {
$(function () {
    makeEditable({
            ajaxUrl: "ajax/meals/",
            restUrl: "rest/profile/meals/",
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
                        "asc"
                    ]
                ]
            }),
            applyFilter: applyFilter
        }
    );
    initFilter();
});

function initFilter() {
    $("#filterForm").submit(function (e) {
        e.preventDefault();
    });
    $("#filterForm > button, input[type='submit']")
        .click(applyFilter);
}

function applyFilter() {
    $.get(context.restUrl + "filter?" + $("#filterForm").serialize(), function (data) {
        context.datatableApi.clear().rows.add(data).draw();
    });
}