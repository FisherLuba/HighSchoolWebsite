const params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop)
});
const editingId = params.id;

function Title() {
    return /*#__PURE__*/React.createElement("h1", {
        className: "page-title text-center"
    }, "What is your question?");
}

class Form extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            title: null,
            content: null,
            category: null,
            subtopics: [],
            anonymous: false,
            tags: null,
            loaded: false,
            fetchedQuestion: null,
            permissions: null,
            submitting: false
        };
        this.updateSubtopics = this.updateSubtopics.bind(this);
    }

    componentDidMount() {
        fetch('/api/permissions', {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.json()).then(data => this.setState({
            permissions: data
        }));
    }

    sendNewQuestion() {
        if (this.state.fetchedQuestion != null) {
            this.updateQuestion();
            return;
        }

        const data = {
            title: this.state.title,
            content: this.state.content,
            category: this.state.category,
            subtopics: this.state.subtopics,
            anonymous: this.state.anonymous,
            tags: []
        };
        fetch('/api/question/new', {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            },
            body: JSON.stringify(data)
        }).then(response => response.json()).then(data => window.location = `/view-question?id=${data.id}`);
    }

    updateQuestion() {
        fetch('/api/question/update', {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            },
            body: JSON.stringify({
                questionId: editingId,
                title: this.state.title,
                content: this.state.content,
                anonymous: this.state.anonymous,
                subtopics: this.state.subtopics
            })
        }).then(response => response.text()).then(data => window.location = `/view-question?id=${editingId}`);
    }

    fetchQuestion() {
        fetch(`/api/question?id=${editingId}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.json()).then(data => {
            this.state.fetchedQuestion = data;
            this.state.title = data.title;
            this.state.content = data.content;
            this.state.category = data.category;
            this.state.anonymous = data.anonymous;
            this.state.subtopics = data.subtopics;
            this.setState({
                tags: data.tags
            });
        });
    }

    Title() {
        const handleChange = event => {
            const value = event.target.value;

            if (value === "") {
                this.state.title = null;
                return;
            }

            this.state.title = value;
        };

        const defaultTitle = this.state.title == null ? "" : this.state.title;
        return /*#__PURE__*/React.createElement("div", {
            className: "col col-md-6"
        }, /*#__PURE__*/React.createElement("input", {
            id: "question-input",
            type: "text",
            className: "form-control text-center",
            placeholder: "Question...",
            onChange: handleChange,
            defaultValue: defaultTitle
        }));
    }

    ExtraInformation() {
        const handleChange = event => {
            const value = event.target.value;

            if (value === "") {
                this.state.content = null;
                return;
            }

            this.state.content = value;
        };

        const defaultExtraInformation = this.state.content == null ? "" : this.state.content;
        return /*#__PURE__*/React.createElement("div", {
            className: "col col-md-6"
        }, /*#__PURE__*/React.createElement("textarea", {
            className: "form-control",
            placeholder: "Extra Information",
            onChange: handleChange,
            defaultValue: defaultExtraInformation
        }));
    }

    AnonymousButton() {
        const handleClick = () => {
            this.state.anonymous = !this.state.anonymous;
        };

        const defaultAnonymous = this.state.anonymous;
        return /*#__PURE__*/React.createElement("div", {
            className: "col col-md-6"
        }, /*#__PURE__*/React.createElement("div", {
            className: "text-center"
        }, /*#__PURE__*/React.createElement("label", null, "Anonymous"), /*#__PURE__*/React.createElement("input", {
            onClick: handleClick,
            type: "checkbox",
            defaultChecked: defaultAnonymous
        })));
    }

    Submit() {
        const handleClick = () => {
            if (this.state.title == null || this.state.content == null || this.state.category == null || this.state.subtopics == null || // this.state.subtopics.length === 0 ||
                this.state.submitting) return;
            this.setState({
                submitting: true
            });
            this.sendNewQuestion();
        };

        const buttonColor = this.state.submitting ? "btn-secondary" : "btn-primary";
        return /*#__PURE__*/React.createElement("div", {
            className: "col col-md-6"
        }, /*#__PURE__*/React.createElement("button", {
            className: `submit-button btn ${buttonColor} text-center`,
            type: "button",
            onClick: handleClick,
            disabled: this.state.submitting
        }, "Submit"));
    }

    updateCategory(category) {
        this.state.subtopics = [];
        this.setState({
            category: category
        });
    }

    updateSubtopics(subtopics) {
        this.setState({
            subtopics: [...subtopics]
        });
    }

    render() {
        if (this.state.permissions == null) return null;

        if (!this.state.permissions["post_question"]) {
            console.log(this.state.permissions);
            // window.location = "/";
            return null;
        }

        if (editingId != null && this.state.loaded == false) {
            this.state.loaded = true;
            this.fetchQuestion();
            return null;
        }

        const onCategoryClick = event => {
            this.updateCategory(event.target.dataset.category);
        };

        return /*#__PURE__*/React.createElement("form", null, /*#__PURE__*/React.createElement("div", {
            className: "d-flex justify-content-center"
        }, /*#__PURE__*/React.createElement(CategoryList, {
            onClick: onCategoryClick
        })), this.state.category != null && /*#__PURE__*/React.createElement("div", {
            className: "d-flex justify-content-center"
        }, /*#__PURE__*/React.createElement("h3", null, this.state.category.charAt(0).toUpperCase() + this.state.category.slice(1))), /*#__PURE__*/React.createElement("div", {
            className: "d-flex justify-content-center"
        }, this.Title()), /*#__PURE__*/React.createElement("div", {
            className: "d-flex justify-content-center"
        }, this.ExtraInformation()), /*#__PURE__*/React.createElement("div", {
            className: "d-flex justify-content-center"
        }, this.AnonymousButton()), /*#__PURE__*/React.createElement("div", {
            className: "d-flex justify-content-center"
        }, this.Submit()));
    }

}

function display() {
    return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(Navbar, null), /*#__PURE__*/React.createElement(Title, null), /*#__PURE__*/React.createElement(Form, null));
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(display());