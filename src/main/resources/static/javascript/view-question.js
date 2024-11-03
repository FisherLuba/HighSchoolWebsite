const params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop)
});
const questionId = params.id;
const pageCategory = params.category;

class TitleArea extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            question: null,
            permissions: null
        };
    }

    componentDidMount() {
        this.getQuestion();
    }

    getQuestion() {
        Promise.all([fetch(`/api/question?id=${questionId}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.json()).then(data => {
            return {
                question: data == null ? undefined : data
            };
        }).catch(() => window.location = "/error"), fetch(`/api/permissions?questionId=${questionId}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'text/plain'
            }
        }).then(response => response.json()).then(data => {
            return {
                permissions: data
            };
        })]).then(promises => {
            const finalObject = {
                question: [],
                permissions: []
            };

            for (let i = 0; i < promises.length; i++) {
                const promise = promises[i];

                if (promise.question != null) {
                    finalObject.question = promise.question;
                    continue;
                }

                if (promise.permissions != null) {
                    finalObject.permissions = promise.permissions;
                }
            }

            this.setState(finalObject);
        });
    }

    approve() {
        fetch(`/api/approve-post?questionId=${this.state.question.questionId}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.json()).then(data => this.setState({
            question: data
        })).catch(e => {});
    }

    unapprove() {
        fetch(`/api/unapprove-post?questionId=${this.state.question.questionId}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.json()).then(data => this.setState({
            question: data
        })).catch(e => {});
    }

    render() {
        if (this.state.permissions == null) return null;
        if (this.state.question === null) return null;

        const handleEditClick = () => {
            window.location = `/submit-question?id=${questionId}`;
        };

        const canEdit = this.state.permissions["edit_self_question"];

        const handleApproveClick = () => {
            this.approve();
        };

        const handleUnapproveClick = () => {
            if (!this.state.permissions["approve_post"]) return;
            this.unapprove();
        };

        const onMouseOver = event => {
            this.setState({
                hoveringOverPost: true
            });
        };

        const onMouseOut = event => {
            this.setState({
                hoveringOverPost: false
            });
        };

        const canApprove = this.state.permissions["approve_post"];
        return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement("div", {
            className: "card text-body no-pointer-on-hover",
            onMouseOver: onMouseOver,
            onMouseOut: onMouseOut
        }, /*#__PURE__*/React.createElement("h3", {
            className: "question-categories text-center"
        }, "Categories: ", this.state.question.subtopics.map(subtopic => /*#__PURE__*/React.createElement(React.Fragment, {
            key: subtopic
        }, /*#__PURE__*/React.createElement("span", {
            className: "badge bg-secondary"
        }, subtopic))), "\xA0", canEdit == true && /*#__PURE__*/React.createElement("span", {
            className: "pointer-on-hover",
            onClick: handleEditClick
        }, "\u270E"), "\xA0", this.state.question.approvedById == null && canApprove === true && this.state.hoveringOverPost && /*#__PURE__*/React.createElement("span", {
            className: "pointer-on-hover badge bg-secondary",
            onClick: handleApproveClick
        }, "Approve"), this.state.question.approvedById != null && /*#__PURE__*/React.createElement("span", {
            className: "pointer-on-hover badge bg-success",
            onClick: handleUnapproveClick
        }, "Approved")), /*#__PURE__*/React.createElement("h1", {
            className: "question-title text-center title"
        }, this.state.question.title), /*#__PURE__*/React.createElement("div", {
            className: "d-flex justify-content-center"
        }, /*#__PURE__*/React.createElement("div", {
            className: "col col-md-6"
        }, /*#__PURE__*/React.createElement("div", {
            className: "question-content vertical-margin"
        }, this.state.question.content)))));
    }

}

class Answer extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            answer: this.props.answer,
            liked: this.props.liked,
            authorName: null,
            deletingTaskId: -1,
            permissions: null
        };
        this.updateLikes = this.updateLikes.bind(this);
    }

    componentDidMount() {
        Promise.all([fetch(`/api/author-name?authorId=${this.state.answer.authorId}&questionId=${this.state.answer.questionId}&answerId=${this.state.answer.answerId}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'text/plain'
            }
        }).then(response => response.text()).then(data => {
            return {
                authorName: data
            };
        }), fetch(`/api/permissions?questionId=${questionId}&answerId=${this.state.answer.answerId}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'text/plain'
            }
        }).then(response => response.json()).then(data => {
            return {
                permissions: data
            };
        })]).then(promises => {
            const finalObject = {
                authorName: [],
                permissions: []
            };

            for (let i = 0; i < promises.length; i++) {
                const promise = promises[i];

                if (promise.authorName != null) {
                    finalObject.authorName = promise.authorName;
                    continue;
                }

                if (promise.permissions != null) {
                    finalObject.permissions = promise.permissions;
                }
            }

            this.setState(finalObject);
        });
    }

    updateLikes(likeAction) {
        fetch(`/api/change-answer-likes?questionId=${this.state.answer.questionId}&answerId=${this.state.answer.answerId}&likeAction=${likeAction}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.json()).then(data => console.log("successfully changed likes: " + data));
    }

    delete() {
        fetch(`/api/delete-answer?questionId=${this.state.answer.questionId}&answerId=${this.state.answer.answerId}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'text/plain'
            }
        }).then(response => response.text()).then(data => {
            if (this.props.onDelete != null) this.props.onDelete(this.state.answer.answerId);
        });
    }

    approve() {
        fetch(`/api/approve-post?questionId=${this.state.answer.questionId}&answerId=${this.state.answer.answerId}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.json()).then(data => this.setState({
            answer: data
        })).catch(e => {});
    }

    unapprove() {
        fetch(`/api/unapprove-post?questionId=${this.state.answer.questionId}&answerId=${this.state.answer.answerId}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.json()).then(data => this.setState({
            answer: data
        })).catch(e => {});
    }

    render() {
        // const copiedLikes = [...this.state.answers].reduce(id -> );
        if (this.state.permissions == null) return null;
        const likeColor = "pointer-on-hover badge " + (this.state.liked ? "bg-primary" : "bg-secondary");

        const onLikeButtonClick = event => {
            if (!this.state.liked) {
                this.state.answer.likes++;
                this.updateLikes("add");
            } else {
                this.state.answer.likes--;
                this.updateLikes("subtract");
            }

            this.setState({
                liked: !this.state.liked
            });
        };

        const handleApproveClick = () => {
            this.approve();
        };

        const handleUnapproveClick = () => {
            if (!this.state.permissions["approve_post"]) return;
            this.unapprove();
        };

        const onMouseOver = event => {
            this.setState({
                hoveringOverPost: true
            });
        };

        const onMouseOut = event => {
            this.setState({
                hoveringOverPost: false
            });
        };

        const canDelete = this.state.permissions["delete_other_question"];
        const canEdit = this.state.permissions["edit_self_question"];
        const canApprove = this.state.permissions["approve_post"];

        const handleDelete = () => {
            if (this.state.deletingTaskId !== -1) {
                this.delete();
                return;
            }

            this.setState({
                deletingTaskId: setTimeout(() => {
                    this.setState({
                        deletingTaskId: -1
                    });
                }, 1000)
            });
        };

        const handleEditClick = () => {
            if (this.props.onClickEdit == null) return;
            this.props.onClickEdit(this.props.answer.answerId, this.props.answer.content, this.props.answer.anonymous);
        };

        const deleteButtonClass = "pointer-on-hover" + (this.state.deletingTaskId !== -1 ? " text-danger" : " text-warning");
        return /*#__PURE__*/React.createElement("div", {
            className: "row justify-content-center response-row",
            onMouseOver: onMouseOver,
            onMouseOut: onMouseOut
        }, /*#__PURE__*/React.createElement("div", {
            className: "col-md-6"
        }, /*#__PURE__*/React.createElement("div", {
            className: "card text-body"
        }, /*#__PURE__*/React.createElement("div", {
            className: "card-title"
        }, /*#__PURE__*/React.createElement("h5", {
            style: {
                display: "inline"
            }
        }, "Author: ", this.state.authorName, this.state.answer.tags.map(badge => /*#__PURE__*/React.createElement("span", {
            key: "badge",
            className: "badge rounded-pill"
        }, badge))), "\xA0", canDelete == true && /*#__PURE__*/React.createElement("span", {
            className: deleteButtonClass,
            onClick: handleDelete
        }, "\u232B"), "\xA0", canEdit == true && /*#__PURE__*/React.createElement("span", {
            className: "pointer-on-hover",
            onClick: handleEditClick
        }, "\u270E"), "\xA0", this.state.answer.approvedById == null && canApprove === true && this.state.hoveringOverPost && /*#__PURE__*/React.createElement("span", {
            className: "pointer-on-hover badge bg-secondary",
            onClick: handleApproveClick
        }, "Approve"), this.state.answer.approvedById != null && /*#__PURE__*/React.createElement("span", {
            className: "pointer-on-hover badge bg-success",
            onClick: handleUnapproveClick
        }, "Approved")), /*#__PURE__*/React.createElement("div", {
            className: "response vertical-margin"
        }, this.state.answer.content), /*#__PURE__*/React.createElement("span", null, /*#__PURE__*/React.createElement("span", {
            className: likeColor,
            onClick: onLikeButtonClick
        }, "Agree: ", this.state.answer.likes)))));
    }

}

class Answers extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            answers: [],
            likedAnswers: [],
            userId: null,
            editingId: null,
            editingAnonymous: false,
            editingContent: null,
            permissions: null
        };
    }

    componentDidMount() {
        this.getAnswers();
    }

    getAnswers() {
        Promise.all([fetch(`/api/answer-data?questionId=${questionId}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.json()).then(data => {
            return {
                answers: data
            };
        }), // .then(data => this.setState({answers: data})),
            fetch(`/api/liked-answers?questionId=${questionId}`, {
                method: 'POST',
                headers: {
                    'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                    'Content-Type': 'application/json; charset=utf-8'
                }
            }).then(response => response.json()).then(data => {
                return {
                    likedAnswers: data
                };
            }), fetch(`/api/user-id`, {
                method: 'POST',
                headers: {
                    'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                    'Content-Type': 'text/plain'
                }
            }).then(response => response.text()).then(data => {
                return {
                    userId: data
                };
            }), fetch(`/api/permissions`, {
                method: 'POST',
                headers: {
                    'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                    'Content-Type': 'text/plain'
                }
            }).then(response => response.json()).then(data => {
                return {
                    permissions: data
                };
            })]).then(promises => {
            const finalObject = {
                answers: [],
                likedAnswers: [],
                userId: null,
                permissions: null
            };

            for (let i = 0; i < promises.length; i++) {
                const promise = promises[i];

                if (promise.answers != null) {
                    finalObject.answers = promise.answers;
                    continue;
                }

                if (promise.likedAnswers != null) {
                    finalObject.likedAnswers = promise.likedAnswers;
                }

                if (promise.userId != null) {
                    finalObject.userId = promise.userId;
                }

                if (promise.permissions != null) {
                    finalObject.permissions = promise.permissions;
                }
            }

            this.setState(finalObject);
        });
    }

    render() {
        if (this.state.permissions == null) return null;

        const onDelete = answerId => {
            this.setState({
                answers: this.state.answers.filter(answer => answer.answerId !== answerId)
            });
        };

        const onClickEdit = (answerId, content, anonymous) => {
            this.state.editingId = answerId;
            this.state.editingContent = content; // this.state.editingAnonymous = anonymous;

            this.setState({
                editingAnonymous: anonymous
            });
        };

        const answers = [];

        for (let i = 0; i < this.state.answers.length; i++) {
            const answer = this.state.answers[i];
            if (this.state.editingId === answer.answerId) continue;
            const isLiked = this.state.likedAnswers.map(answer => answer.answerId).includes(answer.answerId);
            answers.push( /*#__PURE__*/React.createElement(Answer, {
                key: i,
                onClickEdit: onClickEdit,
                onDelete: onDelete,
                liked: isLiked,
                answer: answer
            }));
        }

        const onAnswerSubmit = () => {
            this.state.editingId = null;
            this.state.editingContent = null;
            this.state.editingAnonymous = false;
            this.getAnswers();
        };

        const alreadyAnswered = this.state.answers.filter(answer => answer.authorId === this.state.userId).length !== 0;

        if (alreadyAnswered && this.state.editingId == null) {
            return answers;
        }

        return /*#__PURE__*/React.createElement(React.Fragment, null, answers, this.state.permissions["post_answer"] && /*#__PURE__*/React.createElement(SubmitAnswer, {
            answers: answers,
            content: this.state.editingContent,
            anonymous: this.state.editingAnonymous,
            answerId: this.state.editingId,
            onAnswerSubmit: onAnswerSubmit
        }));
    }

}

class SubmitAnswer extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            answers: [],
            answerContent: null,
            anonymous: false,
            answering: false,
            permissions: null
        };
        this.submitAnswer = this.submitAnswer.bind(this);
    }

    componentDidMount() {
        fetch(`/api/permissions`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'text/plain'
            }
        }).then(response => response.json()).then(data => this.setState({
            permissions: data
        }));
    }

    submitAnswer() {
        if (this.state.answering === true) return;
        this.state.answering = true;

        if (this.props.answerId != null && this.props.answerId != -1) {
            this.updateAnswer();
            return;
        }

        if (this.state.answerContent == null || this.state.answerContent.trim().length === 0) return;
        fetch('/api/answer/new', {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            },
            body: JSON.stringify({
                questionId: questionId,
                content: this.state.answerContent,
                anonymous: this.state.anonymous,
                tags: []
            })
        }).then(response => response.json()).then(data => {
            this.state.answering = false;
            if (this.props.onAnswerSubmit !== null) this.props.onAnswerSubmit();
        });
    }

    updateAnswer() {
        fetch('/api/answer/update', {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            },
            body: JSON.stringify({
                questionId: questionId,
                answerId: this.props.answerId,
                content: this.state.answerContent,
                anonymous: this.state.anonymous
            })
        }).then(response => response.json()).then(data => {
            this.state.answering = false;
            if (this.props.onAnswerSubmit !== null) this.props.onAnswerSubmit();
        });
    }

    AnonymousButton() {
        const handleClick = () => {
            this.state.anonymous = !this.state.anonymous;
        };

        this.state.anonymous = this.props.anonymous;
        return /*#__PURE__*/React.createElement("div", {
            className: "col col-md-6"
        }, /*#__PURE__*/React.createElement("div", {
            className: "text-center"
        }, /*#__PURE__*/React.createElement("label", null, "Anonymous"), /*#__PURE__*/React.createElement("input", {
            onChange: handleClick,
            defaultChecked: this.props.anonymous,
            type: "checkbox"
        })));
    }

    render() {
        if (this.state.permissions == null || this.state.permissions["post_answer"] == false) return null;

        const updateContent = event => {
            this.state.answerContent = event.target.value;
        };

        const answerContent = this.props.content == null ? "" : this.props.content;
        this.state.answerContent = this.props.content;
        return /*#__PURE__*/React.createElement("div", {
            className: "row justify-content-center response-row vertical-margin"
        }, /*#__PURE__*/React.createElement("div", {
            className: "col-md-6"
        }, /*#__PURE__*/React.createElement("form", null, /*#__PURE__*/React.createElement("div", {
            className: "d-flex justify-content-center"
        }, /*#__PURE__*/React.createElement("textarea", {
            className: "form-control",
            placeholder: "Answer...",
            onChange: updateContent,
            defaultValue: answerContent
        })), /*#__PURE__*/React.createElement("div", {
            className: "d-flex justify-content-center"
        }, this.AnonymousButton()), /*#__PURE__*/React.createElement("div", {
            className: "d-flex justify-content-center"
        }, /*#__PURE__*/React.createElement("button", {
            className: "btn dropdown-color text-center vertical-margin",
            type: "button",
            onClick: this.submitAnswer,
            disabled: this.state.answering
        }, "Submit")))));
    }

}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render( /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(Navbar, null), /*#__PURE__*/React.createElement("div", {
    className: "container-fluid mt-3"
}, /*#__PURE__*/React.createElement(TitleArea, null), /*#__PURE__*/React.createElement(Answers, null))));