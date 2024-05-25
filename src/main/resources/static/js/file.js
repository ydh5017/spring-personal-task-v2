let fileCnt = 1;
let totalCnt = 10;

// 파일 선택
function selectFile(element) {

    const file = element.files[0];

    // 1. 파일 선택 창에서 취소 버튼이 클릭된 경우
    if ( !file ) {
        filename.value = '';
        return false;
    }

    // 2. 파일 크기가 10MB를 초과하는 경우
    const fileSize = Math.floor(file.size / 1024 / 1024);
    if (fileSize > 10) {
        alert('10MB 이하의 파일로 업로드해 주세요.');
        filename.value = '';
        element.value = '';
        return false;
    }
}

// 파일 Input 추가
function addFile() {
    if (fileCnt >= totalCnt) {
        alert("10개 이하의 파일만 업로드할 수 있습니다.")
    }else {
        const fileDiv = document.createElement('div');
        fileDiv.innerHTML =`
            <div class="file_input">
                <label> 
                    <input type="file" name="files" onchange="selectFile(this);" />
                </label>
                <span onclick="removeFile(this)" style="margin-right:5px">
                    <svg xmlns="http://www.w3.org/2000/svg" width="30px" fill="red" class="bi bi-x-circle-fill" viewBox="0 0 16 16">
                      <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zM5.354 4.646a.5.5 0 1 0-.708.708L7.293 8l-2.647 2.646a.5.5 0 0 0 .708.708L8 8.707l2.646 2.647a.5.5 0 0 0 .708-.708L8.707 8l2.647-2.646a.5.5 0 0 0-.708-.708L8 7.293 5.354 4.646z"/>
                    </svg>
               </span>
            </div>
        `;
        document.querySelector('.file_list').appendChild(fileDiv);
        fileCnt++;
    }
}

// 파일 Input 삭제
function removeFile(element) {
    const fileAddBtn = element.nextElementSibling;
    if (fileAddBtn) {
        const inputs = element.previousElementSibling.querySelectorAll('input');
        inputs.forEach(input => input.value = '')
        return false;
    }
    element.parentElement.remove();
}

// 파일 목록 조회
function getFileList(scheduleId) {
    if ($(`#${scheduleId}-files-box`).empty()) {
        $.ajax({
            type: 'GET',
            url: '/api/files',
            data: {"scheduleId":scheduleId},
            success: function (response) {
                for (let i = 0; i < response.length; i++) {
                    let message = response[i];
                    let id = message['id'];
                    let scheduleId = message['scheduleId'];
                    let fileName = message['fileName'];
                    let saveName = message['saveName'];
                    let size = message['size'];
                    addFileHTML(id, scheduleId, fileName, saveName, size);
                }
            },error: err => {
                alert(err.responseJSON.message);
            }
        })
    }else {
        $(`#${scheduleId}-files-box`).empty()
    }
}

// 파일 목록 HTML
function addFileHTML(id, scheduleId, fileName, saveName, size) {
    // 1. HTML 태그를 만듭니다.
    let tempHtml = `<div id="${id}-file">
                                <a href="http://localhost:8080/api/files/download/${id}" onclick="">${fileName}</a>
                            </div>`;
    // 2. #cards-box 에 HTML을 붙인다.
    $(`#${scheduleId}-files-box`).append(tempHtml);
}