package controllers

import models.TaskListInMemoryModel
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject.{Inject, Singleton}
import play.api.mvc._
import play.api.i18n._

@Singleton
class TaskList1 @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def login: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.login1())
  }

  def validateLoginGet(username: String, password: String): Action[AnyContent] = Action {
    Ok(s"$username logged in successfully!")
  }

  def validateLoginPost: Action[AnyContent] = Action { implicit request =>
    val postVals = request.body.asFormUrlEncoded
    postVals.map { args =>
      val username = args("username").head
      val password = args("password").head
      if (TaskListInMemoryModel.validateUser(username, password)) {
        Redirect(routes.TaskList1.taskList()).withSession("username" -> username)
      } else {
        Redirect(routes.TaskList1.login()).flashing("error" -> "Invalid username or password.")
      }
    }.getOrElse(Redirect(routes.TaskList1.login()))
  }

  def createUser: Action[AnyContent] = Action { implicit request =>
    val postVals = request.body.asFormUrlEncoded
    postVals.map { args =>
      val username = args("username").head
      val password = args("password").head
      if (TaskListInMemoryModel.createUser(username, password)) {
        Redirect(routes.TaskList1.taskList()).withSession("username" -> username)
      } else {
        Redirect(routes.TaskList1.login()).flashing("error" -> "User creation failed.")
      }
    }.getOrElse(Redirect(routes.TaskList1.login()))
  }

  def taskList: Action[AnyContent] = Action { implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption.map { username =>
      val tasks = TaskListInMemoryModel.getTasks(username)
      Ok(views.html.taskList1(tasks))
    }.getOrElse(Redirect(routes.TaskList1.login()))
  }

  def logout: Action[AnyContent] = Action {
    Redirect(routes.TaskList1.login()).withNewSession
  }
}
