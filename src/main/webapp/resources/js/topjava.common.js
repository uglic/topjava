let context, form;

function makeEditable(ctx) {
    context = ctx;
    form = $('#detailsForm');
    $(".delete").click(function () {
        if (confirm('Are you sure?')) {
            deleteRow($(this).closest("tr").attr("id"));
        }
    });

    $(document).ajaxError(function (event, jqXHR, options, jsExc) {
        failNoty(jqXHR);
    });

    // solve problem with cache in IE: https://stackoverflow.com/a/4303862/548473
    $.ajaxSetup({cache: false});
    if (ctx.filter !== undefined) initFilter(ctx);
}

function add() {
    form.find(":input").val("");
    $("#editRow").modal();
}

function deleteRow(id) {
    $.ajax({
        url: context.ajaxUrl + id,
        type: "DELETE"
    }).done(function () {
        applyFilter(context);
        successNoty("Deleted");
    });
}

function save() {
    $.ajax({
        type: "POST",
        url: context.ajaxUrl,
        data: form.serialize()
    }).done(function () {
        $("#editRow").modal("hide");
        applyFilter(context);
        successNoty("Saved");
    });
}

function applyFilter(ctx) {
    let query = ctx.ajaxUrl;
    if (ctx.filter !== undefined) {
        query = ctx.ajaxUrl + "filter?" + ctx.filter.serialize();
    }
    $.get(query, updateData);
}


function updateData(data) {
    context.datatableApi.clear().rows.add(data).draw();
}

function initFilter(ctx) {
    ctx.filter.submit((e) => e.preventDefault());
    ctx.filter.children("button[type='submit'], button[type='reset']").click(function (e) {
            if ($(this).attr("type") === "reset") $(this).closest("form")[0].reset();
            applyFilter(ctx);
        }
    );
}

let failedNote;

function closeNoty() {
    if (failedNote) {
        failedNote.close();
        failedNote = undefined;
    }
}

function successNoty(text) {
    closeNoty();
    new Noty({
        text: "<span class='fa fa-lg fa-check'></span> &nbsp;" + text,
        type: 'success',
        layout: "bottomRight",
        timeout: 1000
    }).show();
}

function failNoty(jqXHR) {
    closeNoty();
    failedNote = new Noty({
        text: "<span class='fa fa-lg fa-exclamation-circle'></span> &nbsp;Error status: " + jqXHR.status,
        type: "error",
        layout: "bottomRight"
    }).show();
}