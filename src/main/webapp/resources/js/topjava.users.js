// $(document).ready(function () {
$(function () {
    makeEditable({
            ajaxUrl: "ajax/admin/users/",
            restUrl: "rest/admin/users/",
            datatableApi: $("#datatable").DataTable({
                "paging": false,
                "info": true,
                "columns": [
                    {
                        "data": "name"
                    },
                    {
                        "data": "email"
                    },
                    {
                        "data": "roles"
                    },
                    {
                        "data": "enabled"
                    },
                    {
                        "data": "registered"
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
            updateTableStyles: updateTableStyles
        }
    );
    initHandlers();
    updateTableStyles();
});

function initHandlers() {
    $("tr.row-user > input,[type='checkbox'][name='enabled']")
        .change(function (e) {
            setEnabled($(this).closest("tr").attr("id"), this.checked);
            updateTable();
        });
}

function setEnabled(id, newStatus) {
    $.get(context.restUrl + id, function (dataFrom) {
        dataFrom.enabled = newStatus;
        $.ajax({
            type: "PUT",
            url: context.restUrl + id,
            processData: false,
            contentType: "application/json",
            data: JSON.stringify(dataFrom)
        }).done(function () {
            updateTable();
        });
    });
}

function updateTableStyles() {
    $("#datatable input:checkbox:not(:checked)").closest("tr").addClass("table-warning");
    $("#datatable input:checkbox:checked").closest("tr").removeClass("table-warning");
}