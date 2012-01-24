package testtools.matching

import org.junit.internal.matchers.TypeSafeMatcher
import org.hamcrest.{Description, Matcher}

object Matchers {
  def isEmpty: Matcher[Iterable[_]] = new TypeSafeMatcher[Iterable[_]]() {
    def describeTo(description: Description) {
      description.appendText("An empty iterable")
    }

    def matchesSafely(item: Iterable[_]) = {
      item.isEmpty
    }
  }
}