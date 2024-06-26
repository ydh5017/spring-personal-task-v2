// 댓글 등록
function addComment(scheduleId, type) {

    validUserLogin();

    const content = $(`#${scheduleId}-${type}-comment-input`).val()

    const data = {"scheduleId": scheduleId, "content": content};

    $.ajax({
        type: "POST",
        url: "/api/comments",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
            alert('댓글 등록에 성공하였습니다.');
            getCommentList(scheduleId, type);
        },error: err => {
            alert(err.responseJSON.message);
        }
    });

}

// 댓글 목록 조회
function getCommentList(scheduleId, type) {
    $(`#${scheduleId}-all-comments-box`).empty()
    $(`#${scheduleId}-user-comments-box`).empty()
    $(`#${scheduleId}-search-comments-box`).empty()

    let tempHtml = `<input type="text" id="${scheduleId}-${type}-comment-input" placeholder="댓글을 입력해주세요.">
                            <button type="button" class="schedule-btn" onclick="addComment(${scheduleId}, '${type}')">등록</button>`;

    if (type === "all") {
        $(`#${scheduleId}-all-comments-box`).append(tempHtml);
    } else if (type === "user") {
        $(`#${scheduleId}-user-comments-box`).append(tempHtml);
    }else {
        $(`#${scheduleId}-search-comments-box`).append(tempHtml);
    }

    $.ajax({
        type: 'GET',
        url: '/api/comments',
        data: {"scheduleId":scheduleId},
        success: function (response) {
            for (let i = 0; i <= response.length; i++) {
                let message = response[i];
                let id = message['id'];
                let content = message['content'];
                let userName = message['userName'];
                let modifiedAt = message['modifiedAt'];
                addCommentHTML(scheduleId, id, content, userName, modifiedAt, type);
            }
        },error: err => {
            alert(err.responseJSON.message);
        }
    })
}

// 댓글 목록 HTML
function addCommentHTML(scheduleId, id, content, userName, modifiedAt, type) {
    // 1. HTML 태그를 만듭니다.
    let tempHtml = `<div id="comment-${id}" class="content">
                                <span>작성자 : ${userName}</span>
                                <span>수정일 : ${modifiedAt}</span>
                                <br>
                                <span>${content}</span>
                                <div id="${id}-${type}-mod-comment-box" class="none">
                                    <input id="${id}-${type}-mod-comment-input" type="text" value="${content}">
                                    <button type="button" class="schedule-btn" onclick="modComment(${id}, '${type}')">수정</button>
                                </div>
                                <button type="button" class="schedule-btn" onclick="modCommentInput(${scheduleId}, ${id}, '${type}')">수정</button>
                                <button type="button" class="schedule-btn" onclick="deleteComment(${id})">삭제</button>
                            </div>`;
    // 2. #cards-box 에 HTML을 붙인다.
    $(`#${scheduleId}-all-comments-box`).append(tempHtml);
    $(`#${scheduleId}-user-comments-box`).append(tempHtml);
    $(`#${scheduleId}-search-comments-box`).append(tempHtml);
}

// 수정 Input
function modCommentInput(scheduleId, id, type) {
    validUserLogin();

    if (type === "all") {
        $(`#${scheduleId}-user-comments-box`).empty()
        $(`#${scheduleId}-search-comments-box`).empty()
    } else if (type === "user") {
        $(`#${scheduleId}-all-comments-box`).empty()
        $(`#${scheduleId}-search-comments-box`).empty()
    }else {
        $(`#${scheduleId}-all-comments-box`).empty()
        $(`#${scheduleId}-user-comments-box`).empty()
    }

    $(`#${id}-${type}-mod-comment-box`).show();
}

// 댓글 수정
function modComment(id, type) {
    const content = $(`#${id}-${type}-mod-comment-input`).val();

    $.ajax({
        type: 'PUT',
        url: `/api/comments/${id}`,
        contentType: "application/json",
        data: JSON.stringify({"content": content}),
        success: function (response) {
            alert('댓글 수정에 성공하였습니다.');
            window.location.reload();
        },error: err => {
            alert(err.responseJSON.message);
        }
    })
}

// 댓글 삭제
function deleteComment(id) {
    validUserLogin();
    $.ajax({
        type: 'DELETE',
        url: `/api/comments/${id}`,
        success: function (response) {
            alert('댓글 삭제에 성공하였습니다.');
            window.location.reload();
        },error: err => {
            alert(err.responseJSON.message);
        }
    })
}