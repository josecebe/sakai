import {SakaiElement} from "/webcomponents/sakai-element.js";
import {html} from "/webcomponents/assets/lit-element/lit-element.js";
import {tr} from "./sakai-rubrics-language.js";

export class SakaiRubricCriterionRatingEdit extends SakaiElement {

  constructor() {

    super();

    this._rating;
  }

  static get properties() {

    return {
      rating: { type: Object },
      criterionId: { type: String },
      minpoints: { type: Number },
      maxpoints: { type: Number },
    };
  }

  set rating(newValue) {

    if (newValue.new) {
      newValue.title = "New Rating";
    }
    var oldValue = this._rating;
    this._rating = newValue;
    this.requestUpdate("rating", oldValue);
    if (this._rating.new) {
      this.updateComplete.then(() => this.querySelector(".edit").click() );
    }
  }

  get rating() { return this._rating; }

  render() {

    return html`
      <span tabindex="0" role="button" class="edit fa fa-edit" @click="${this.editRating}" title="${tr("edit_rating")} ${this.rating.title}"></span>

      <div id="edit_criterion_rating_${this.rating.id}" class="popover rating-edit-popover bottom">
        <div class="arrow"></div>
        <div class="popover-title">
          <div class="buttons">
            <button class="btn btn-primary btn-xs save" @click="${this.saveEdit}">
              <sr-lang key="save">Save</sr-lang>
            </button>
            <button class="delete" @click="${this.deleteRating}">
              <sr-lang key="remove">Remove</sr-lang>
            </button>
            <button class="btn btn-link btn-xs cancel" @click="${this.cancelEdit}">
              <sr-lang key="cancel">Cancel</sr-lang>
            </button>
          </div>
        </div>
        <div class="popover-content form">
          <div class="first-row">
              <div class="form-group title">
                <label for="rating-title">
                  <sr-lang key="rating_title">Rating Title</sr-lang>
                </label>
                <input type="text" id="rating-title-${this.rating.id}" class="form-control" .value="${this.rating.title}">
              </div>
              <div class="form-group points">
                <label for="rating-points">
                  <sr-lang key="points">Points</sr-lang>
                </label>
                <input type="number" id="rating-points-${this.rating.id}" class="form-control hide-input-arrows" name="quantity" .value="${this.rating.points}" min="${this.minpoints}" max="${this.maxpoints}">
              </div>
          </div>
          <div class="form-group">
            <label for="">
              <sr-lang key="rating_description">Rating Description</sr-lang>
            </label>
            <textarea name="" id="rating-description-${this.rating.id}" class="form-control">${this.rating.description}</textarea>
          </div>
        </div>
      </div>
    `;
  }

  closeOpen() {
    $('.show-tooltip .cancel').click();
  }

  editRating(e) {

    e.stopPropagation();

    if (!this.classList.contains("show-tooltip")) {
      this.closeOpen();
      this.classList.add("show-tooltip");
      var popover = $(`#edit_criterion_rating_${this.rating.id}`);

      rubrics.css(popover[0], {
        'top': e.target.offsetTop + 20 + "px",
        'left': (e.target.offsetLeft + e.target.offsetWidth/2 - popover[0].offsetWidth/2) + "px",
      });

      popover.show();
      var titleinput = this.querySelector('[type="text"]');
      titleinput.focus();
      titleinput.setSelectionRange(0, titleinput.value.length);

    } else {
      this.hideToolTip();
    }
  }

  hideToolTip() {

    $(`#edit_criterion_rating_${this.rating.id}`).hide();
    this.classList.remove("show-tooltip");
  }

  resetFields() {

    document.getElementById(`rating-title-${this.rating.id}`).value = this.rating.title;
    document.getElementById(`rating-points-${this.rating.id}`).value = this.rating.points;
    document.getElementById(`rating-description-${this.rating.id}`).value = this.rating.description;
  }

  cancelEdit(e) {

    e.stopPropagation();
    this.hideToolTip();
    this.resetFields();
  }

  saveEdit(e) {

    e.stopPropagation();

    this.rating.title = document.getElementById(`rating-title-${this.rating.id}`).value;
    this.rating.points = document.getElementById(`rating-points-${this.rating.id}`).value;
    this.rating.description = document.getElementById(`rating-description-${this.rating.id}`).value;
    this.rating.criterionId = this.criterionId;

    this.resetFields();

    this.dispatchEvent(new CustomEvent('save-rating', {detail: this.rating}));
    this.hideToolTip();
  }

  deleteRating(e) {

    e.stopPropagation();

    this.rating.criterionId = this.criterionId;
    this.dispatchEvent(new CustomEvent('delete-rating', {detail: this.rating}));
    this.hideToolTip();
  }
}

customElements.define("sakai-rubric-criterion-rating-edit", SakaiRubricCriterionRatingEdit);