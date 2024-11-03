const params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop)
});
const pageCategory = params.category == null ? "recent" : params.category;

function Title() {
    return /*#__PURE__*/React.createElement("h1", {
        className: "category-title title text-center"
    }, pageCategory.charAt(0).toUpperCase() + pageCategory.slice(1));
}

let pageNumber = 0;
let viewAmount = 10;

class Question extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            question: this.props.question,
            authorName: null,
            permissions: null,
            deletingTaskId: -1,
            liked: this.props.liked,
            hoveringOverPost: false
        };
    }

    componentDidMount() {
        Promise.all([fetch(`/api/author-name?authorId=${this.props.question.authorId}&questionId=${this.props.question.questionId}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'text/plain'
            }
        }).then(response => response.text()).then(data => {
            return {
                authorName: data
            };
        }), fetch(`/api/permissions?questionId=${this.props.question.questionId}`, {
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

    delete() {
        fetch(`/api/delete-question?questionId=${this.props.question.questionId}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'text/plain'
            }
        }).then(response => response.text()).then(data => {
            if (this.props.onDelete != null) this.props.onDelete(this.props.question.questionId);
        });
    }

    updateLikes(likeAction) {
        fetch(`/api/change-question-likes?questionId=${this.state.question.questionId}&likeAction=${likeAction}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.json()).then(data => console.log("successfully changed likes: " + data));
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
        const likeColor = "pointer-on-hover badge " + (this.state.liked ? "bg-primary" : "bg-secondary");

        const onLikeButtonClick = event => {
            if (!this.state.liked) {
                this.state.question.likes++;
                this.updateLikes("add");
            } else {
                this.state.question.likes--;
                this.updateLikes("subtract");
            }

            this.setState({
                liked: !this.state.liked
            });
        };

        const handleClick = () => {
            window.location = `view-question?id=${this.props.question.questionId}`;
        };

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

        const deleteButtonClass = "pointer-on-hover" + (this.state.deletingTaskId !== -1 ? " text-danger" : " text-warning");

        const handleEditClick = () => {
            window.location = `/submit-question?id=${this.props.question.questionId}`;
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
        return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement("div", {
            className: "container-fluid mt-3"
        }, /*#__PURE__*/React.createElement("div", {
            className: "row justify-content-center vertical-margin question-snapshot"
        }, /*#__PURE__*/React.createElement("div", {
            className: "col-md-6"
        }, /*#__PURE__*/React.createElement("div", {
            className: "card text-body no-pointer-on-hover",
            onMouseOver: onMouseOver,
            onMouseOut: onMouseOut
        }, /*#__PURE__*/React.createElement("div", {
            className: "card-title"
        }, /*#__PURE__*/React.createElement("h5", {
            style: {
                display: "inline"
            }
        }, this.state.question.title, this.state.question.tags.map(badge => /*#__PURE__*/React.createElement(React.Fragment, null, "\xA0", /*#__PURE__*/React.createElement("span", {
            key: badge,
            className: "badge bg-secondary"
        }, badge)))), "\xA0", canDelete === true && /*#__PURE__*/React.createElement("span", {
            className: deleteButtonClass,
            onClick: handleDelete
        }, "\u232B"), "\xA0", canEdit === true && /*#__PURE__*/React.createElement("span", {
            className: "pointer-on-hover",
            onClick: handleEditClick
        }, "\u270E"), "\xA0", this.state.question.approvedById == null && canApprove === true && this.state.hoveringOverPost && /*#__PURE__*/React.createElement("span", {
            className: "pointer-on-hover badge bg-secondary",
            onClick: handleApproveClick
        }, "Approve"), this.state.question.approvedById != null && /*#__PURE__*/React.createElement("span", {
            className: "pointer-on-hover badge bg-success",
            onClick: handleUnapproveClick
        }, "Approved")), /*#__PURE__*/React.createElement("div", {
            className: "card-title pointer-on-hover",
            onClick: handleClick
        }, /*#__PURE__*/React.createElement("h6", null, "Author: ", this.state.authorName)), /*#__PURE__*/React.createElement("div", {
            className: "pointer-on-hover",
            onClick: handleClick
        }, this.state.question.content), /*#__PURE__*/React.createElement("span", null, /*#__PURE__*/React.createElement("span", {
            className: likeColor,
            onClick: onLikeButtonClick
        }, "Agree: ", this.state.question.likes)))))));
    }

}

class Questions extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            questions: [],
            subtopics: [],
            likedQuestions: [],
            hasNextPage: true
        };
    }

    getQuestions() {
        this.state.subtopics = this.props.subtopics;
        const append = this.state.subtopics == null || this.state.subtopics.length === 0 ? "" : "subtopics=" + this.state.subtopics.join(",");
        const url = `/api/question-data?category=${pageCategory}&totalQuestions=${viewAmount}&pageNumber=${pageNumber}&` + append;
        Promise.all([fetch(url, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.json()).then(data => {
            return {
                questions: data
            };
        }), fetch(`/api/liked-questions`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.json()).then(data => {
            return {
                likedQuestions: data
            };
        })]).then(promises => {
            const finalObject = {
                questions: [],
                likedQuestions: []
            };

            for (let i = 0; i < promises.length; i++) {
                const promise = promises[i];

                if (promise.questions != null) {
                    finalObject.questions = promise.questions;
                    continue;
                }

                if (promise.likedQuestions != null) {
                    finalObject.likedQuestions = promise.likedQuestions;
                }
            }

            this.setState(finalObject);
        });
    }

    render() {
        if (this.props.subtopics !== this.state.subtopics && !this.props.searching) {
            this.getQuestions();
            return;
        }

        if (this.props.searching) {
            this.state.questions = this.props.questions == null ? this.state.questions : this.props.questions;
        }

        const onDelete = questionId => {
            this.setState({
                questions: this.state.questions.filter(question => question.questionId !== questionId)
            });
        };

        if (this.state.questions.length === 0) {
            if (pageNumber > 0) {
                pageNumber--;
                this.state.hasNextPage = false;
                this.getQuestions();
            }
        }

        const onClickNext = () => {
            pageNumber++;
            this.getQuestions();
        };

        const onClickPrevious = () => {
            if (pageNumber === 0) return;
            pageNumber--;
            this.getQuestions();
            this.state.hasNextPage = true;
        };

        const questions = [];

        for (let i = 0; i < this.state.questions.length; i++) {
            const question = this.state.questions[i];
            const isLiked = this.state.likedQuestions.map(question => question.questionId).includes(question.questionId);
            questions.push( /*#__PURE__*/React.createElement(Question, {
                key: question.questionId,
                onDelete: onDelete,
                question: question,
                liked: isLiked
            }));
        }

        return /*#__PURE__*/React.createElement(React.Fragment, null, questions, /*#__PURE__*/React.createElement("div", {
            className: "container mt-3 text-center vertical-margin"
        }, pageNumber > 0 && /*#__PURE__*/React.createElement("button", {
            className: "btn btn-primary vertical-margin",
            onClick: onClickPrevious
        }, "Previous"), this.state.hasNextPage && this.state.questions.length > 0 && /*#__PURE__*/React.createElement("button", {
            className: "btn btn-primary vertical-margin",
            onClick: onClickNext
        }, "Next")));
    }

}

class SearchBar extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            query: ""
        };
        this.getQuestions = this.getQuestions.bind(this);
    }

    componentDidMount() {}

    getQuestions() {
        if (this.state.query.length === 0) return;
        const append = this.props.subtopics == null || this.props.subtopics.length === 0 ? "" : "&subtopics=" + this.props.subtopics.join(",");
        const url = `/api/question-data/search?category=${pageCategory}&totalQuestions=${viewAmount}&pageNumber=${pageNumber}&query=${this.state.query}` + append;
        fetch(url, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.json()).then(data => {
            if (this.props.onSearch == null) return;
            this.props.onSearch(data);
        });
    }

    render() {
        const onType = event => {
            this.state.query = event.target.value;
        };

        return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement("div", {
            className: "d-flex justify-content-center"
        }, /*#__PURE__*/React.createElement("input", {
            onChange: onType,
            placeholder: "Search...",
            type: "text"
        })), /*#__PURE__*/React.createElement("div", {
            className: "d-flex justify-content-center"
        }, /*#__PURE__*/React.createElement("button", {
            className: "button btn-primary",
            onClick: this.getQuestions
        }, "Search")));
    }

}

function SubmitQuestion() {
    return /*#__PURE__*/React.createElement("div", {
        className: "container-fluid mt-3 text-center"
    }, /*#__PURE__*/React.createElement("a", {
        href: "submit-question",
        className: "dropdown-color btn"
    }, "Post Question"));
}

class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            subtopics: [],
            questions: [],
            searching: false
        };
    }

    componentDidMount() {}

    getQuestions() {}

    render() {
        const onSubtopicCheck = subtopics => {
            this.setState({
                subtopics: [...subtopics]
            });
        };

        const onSearch = questions => {
            this.state.searching = true;
            this.setState({
                questions: questions
            });
        };

        return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(Navbar, null), /*#__PURE__*/React.createElement(Title, null), /*#__PURE__*/React.createElement(SearchBar, {
            subtopics: this.state.subtopics,
            onSearch: onSearch
        }), /*#__PURE__*/React.createElement(SubtopicsDropDown, {
            onSubtopicsUpdate: onSubtopicCheck,
            pageCategory: pageCategory
        }), /*#__PURE__*/React.createElement(SubmitQuestion, null), /*#__PURE__*/React.createElement(Questions, {
            searching: this.state.searching,
            questions: this.state.questions,
            subtopics: this.state.subtopics
        }));
    }

}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render( /*#__PURE__*/React.createElement(Page, null));