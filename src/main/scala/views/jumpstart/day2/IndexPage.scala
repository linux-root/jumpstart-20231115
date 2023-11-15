package views.jumpstart.day2

import com.greenfossil.data.mapping.Mapping
import com.greenfossil.htmltags.{*, given}
import com.greenfossil.jumpstart.day2.Task
import com.greenfossil.thorium.{*, given}
import controllers.jumpstart.day2.HomeController

import java.time.format.DateTimeFormatter

/**
 * This is to simulate an HTML Page
 */
case class IndexPage(tasks: List[Task], addTaskMapping: Mapping[String]):
  def render: Tag = html(
    head(
      Tags2.title("To Do"),
      link(rel := "shortcut icon", href := "data:;base64,iVBORw0KGgo="),
      link(rel := "stylesheet", href := "https://cdn.jsdelivr.net/npm/fomantic-ui@2.9.3/dist/semantic.min.css"),
      Tags2.style(
        // For advanced developer, you can add extra CSS style below
        raw(
          """
            | td { padding: 10px !important; border-right: 1px solid rgba(0,0,0,0.3); border-top: 1px solid rgba(0,0,0,0.5) }
            | td:nth-child(3) { border-right: none }
            | td:nth-child(1) { border-top: none }
            | table { border: 1px solid rgba(0,0,0,0.5) !important }
            | button { cursor: pointer; background: none; border-radius: 4px; padding: 8px; margin-right: 10px; border: 1px solid  rgba(0,0,0,0.7)  }
            |""".stripMargin)
      )
    ),
    body(marginTop := 2.rem)(
      div(cls := "ui container")(
        h1(cls := "ui dividing header", "To Do List"),

        form(cls := "ui form", method := "POST", action := HomeController.createTask.endpoint.url)(
          div(cls := "field", ifTrue(addTaskMapping.hasErrors, cls := "error"))(
            div(cls := "ui fluid action input")(
              input(tpe := "text", name := "task", placeholder := "Enter task"),
              button(cls := "ui primary button", "Add")
            )
          )
        ),

        h3(cls := "ui header", "Tasks: ", tasks.size),

        /*
         * Implement a table that lists down in a table, with the below structure:
         * table[class="ui very basic table"]:
         *    tbody:
         *        tr:
         *            td[class="collapsing] > Task index, starting at 1
         *            td > Task description [Strikethrough if the task is completed]
         *            td[class="collapsing"]
         *                button > Complete [Marks task as completed]
         *                button > Delete [Deletes task]
         */
         table(cls := "ui very basic table",
           tbody(
             tasks.map{task =>
               val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
               val completeUrl = HomeController.completeTask(task.id).endpoint.url
               val deleteUrl = HomeController.deleteTask(task.id).endpoint.url
               tr(
                 td(cls := "collapsing", task.id),
                 td(cls := "collapsing", task.description),
                 td(cls := "collapsing", if (task.isCompleted) s"Completed at ${task.completedOn.map(_.format(dateTimeFormatter)).get}" else "Pending"),
                 td(cls := "collapsing", s"Created at ${task.created.format(dateTimeFormatter)}"),
                 td(cls := "collapsing",
                   button("Complete", onclick := s"completeTask('$completeUrl')"),
                   button("Delete", onclick := s"deleteTask('$deleteUrl')"),
                 ),
               )
             }
           )
         )

      ),

      script(tpe := "text/javascript", src := "https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.min.js"),
      script(tpe := "text/javascript", src := "https://cdn.jsdelivr.net/npm/fomantic-ui@2.9.3/dist/semantic.min.js"),
      script(tpe := "text/javascript",
        raw(
          s"""
           function completeTask(url){
               const xhr = new XMLHttpRequest();
               xhr.open('PUT', url);
               xhr.onload = () => {
               if(xhr.status === 200) {
                 console.log('completed the task')
                 location.reload()
                } else {
                  alert('bad thing happened')
                }
               }
                xhr.send();
            }
           function deleteTask(url){
             const xhr = new XMLHttpRequest();
             xhr.open('DELETE', url);
             xhr.onload = () => {
             if(xhr.status === 200) {
                 console.log('deleted the task')
                 location.reload()
              } else {
                alert('bad thing happened')
              }
            }
             xhr.send();
           }""")
      )
    )
  )
