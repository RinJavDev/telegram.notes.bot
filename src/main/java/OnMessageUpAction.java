import org.telegram.telegrambots.api.objects.Message;

public interface OnMessageUpAction {
      void addNewNote(Message message);
      void removeNote(Message message);
    void getNotesFromId(Message message);
}
