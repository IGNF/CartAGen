package fr.ign.cogit.cartagen.collagen.components.registry;

import java.util.Comparator;

public class DescrProcessPostComparator implements
    Comparator<ProcessCapabDescription> {

  private PostConditionProcess post;

  public DescrProcessPostComparator(PostConditionProcess post) {
    super();
    this.post = post;
  }

  @Override
  public int compare(ProcessCapabDescription arg0, ProcessCapabDescription arg1) {
    PostConditionProcess post0 = null;
    for (PostConditionProcess p : arg0.getPostConditions()) {
      if (p.getElement().equals(this.post.getElement())) {
        post0 = p;
        break;
      }
    }
    PostConditionProcess post1 = null;
    for (PostConditionProcess p : arg1.getPostConditions()) {
      if (p.getElement().equals(this.post.getElement())) {
        post1 = p;
        break;
      }
    }
    if (post0 == null) {
      return -1;
    }
    return post0.compareTo(post1);
  }
}
