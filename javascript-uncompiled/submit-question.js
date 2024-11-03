const params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
});
const editingId = params.id;

function Title() {
    return (<h1 className={"page-title text-center"}>What is your question?</h1>);
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
            }
        )
            .then(response => response.json())
            .then(data => this.setState({permissions: data}));
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
        }

        fetch('/api/question/new', {
                method: 'POST',
                headers: {
                    'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                    'Content-Type': 'application/json; charset=utf-8'
                },
                body: JSON.stringify(data)
            }
        )
            .then(response => response.json())
            .then(data => window.location = `/view-question?id=${data.id}`);
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
            }
        )
            .then(response => response.text())
            .then(data => window.location = `/view-question?id=${editingId}`);
    }

    fetchQuestion() {
        fetch(`/api/question?id=${editingId}`, {
                method: 'POST',
                headers: {
                    'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                    'Content-Type': 'application/json; charset=utf-8'
                }
            }
        )
            .then(response => response.json())
            .then(data => {
                this.state.fetchedQuestion = data;
                this.state.title = data.title;
                this.state.content = data.content;
                this.state.category = data.category;
                this.state.anonymous = data.anonymous;
                this.state.subtopics = data.subtopics;
                this.setState({tags: data.tags});
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
        return (<div className={"col col-md-6"}>
            <input id={"question-input"} type={"text"} className={"form-control text-center"}
                   placeholder={"Question..."} onChange={handleChange} defaultValue={defaultTitle}/>
        </div>)
    }

    ExtraInformation() {

        const handleChange = event => {
            const value = event.target.value;
            if (value === "") {
                this.state.content = null;
                return;
            }

            this.state.content = value;
        }


        const defaultExtraInformation = this.state.content == null ? "" : this.state.content;

        return (<div className={"col col-md-6"}>
            <textarea className={"form-control"} placeholder={"Extra Information"} onChange={handleChange}
                      defaultValue={defaultExtraInformation}/>
        </div>)
    }

    AnonymousButton() {
        const handleClick = () => {
            this.state.anonymous = !this.state.anonymous;
        }

        const defaultAnonymous = this.state.anonymous;

        return (<div className={"col col-md-6"}>
            <div className={"text-center"}>
                <label>Anonymous</label><input onClick={handleClick} type="checkbox"
                                               defaultChecked={defaultAnonymous}/>
            </div>
        </div>)
    }

    Submit() {
        const handleClick = () => {
            if (this.state.title == null ||
                this.state.content == null ||
                this.state.category == null ||
                this.state.subtopics == null ||
                // this.state.subtopics.length === 0 ||
                this.state.submitting
            ) return;
            this.setState({submitting: true});
            this.sendNewQuestion();
        }

        const buttonColor = (this.state.submitting ? "btn-secondary" : "btn-primary");

        return (<div className={"col col-md-6"}>
            <button className={`submit-button btn ${buttonColor} text-center`} type={"button"}
                    onClick={handleClick} disabled={this.state.submitting}>Submit
            </button>
        </div>)
    }

    updateCategory(category) {
        this.state.subtopics = [];
        this.setState({category: category});
    }

    updateSubtopics(subtopics) {
        this.setState({subtopics: [...subtopics]});
    }

    render() {
        if (this.state.permissions == null) return null;
        if (!this.state.permissions["post_question"]) {
            window.location = "/";
            return null;
        }
        if (editingId != null && this.state.loaded == false) {
            this.state.loaded = true;
            this.fetchQuestion();
            return null;
        }
        const onCategoryClick = event => {
            this.updateCategory(event.target.dataset.category);
        }
        return (<form>
            <div className={"d-flex justify-content-center"}>
                <CategoryList onClick={onCategoryClick}/>
            </div>
            {this.state.category != null &&
            <div className={"d-flex justify-content-center"}>
                <h3>{this.state.category.charAt(0).toUpperCase() + this.state.category.slice(1)}</h3>
            </div>
            }
            {/*<div className={"d-flex justify-content-center"}>*/}
            {/*    {this.state.category != null &&*/}
            {/*    <SubtopicsDropDown checked={this.state.subtopics} onSubtopicsUpdate={this.updateSubtopics}*/}
            {/*                       pageCategory={this.state.category}/>*/}
            {/*    }*/}
            {/*</div>*/}
            <div className={"d-flex justify-content-center"}>
                {this.Title()}
            </div>
            <div className={"d-flex justify-content-center"}>
                {this.ExtraInformation()}
            </div>
            <div className={"d-flex justify-content-center"}>
                {this.AnonymousButton()}
            </div>
            <div className={"d-flex justify-content-center"}>
                {this.Submit()}
            </div>
        </form>);
    }
}

function display() {
    return (
        <React.Fragment>
            <Navbar/>
            <Title/>
            <Form/>
        </React.Fragment>
    )
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(display());
