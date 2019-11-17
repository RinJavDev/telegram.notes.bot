import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.sql.*;

public class Bot extends TelegramLongPollingBot implements OnMessageUpAction {
    static Statement statement;
    public static void main(String[] args) {
        DB_Notes db_notes = new DB_Notes();
        db_notes.connect();
        statement = db_notes.getStatement();
        ///* Прокси
        System.getProperties().put("proxySet", "true");
        System.getProperties().put("socksProxyHost", "127.0.0.1");
        System.getProperties().put("socksProxyPort", "9150");
       //

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
   public void sendMsg(Message messagem, String text){
        SendMessage sendMessage=new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(messagem.getChatId().toString());
       // sendMessage.setReplyToMessageId(messagem.getMessageId());
        sendMessage.setText(text);
       try {
           sendMessage(sendMessage);
       } catch (TelegramApiException e) {
           e.printStackTrace();
       }


   }
   @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if(message!=null && message.hasText()){
            String text = message.getText();

            if(text.startsWith("/add")) addNewNote(message);else
            if(text.startsWith("/list")) getNotesFromId(message);else
            if(text.startsWith("/delete")) removeNote(message);else
                sendMsg(message,"Неверная команда,список команд:\n" +
                        "/add [ваша заметка] ->(Добавить заметку)\n"+
                        "/delete [номер заметки] -> (Удалить заметку заметку)\n"+
                        "/list ->(Показать ваши заметки)\n"
                );
        }

    }
    @Override
    public void addNewNote(Message message){
    try {
        statement.executeUpdate("INSERT INTO notes(USER_ID, note) VALUES("+message.getChatId()+", '"+message.getText().substring(4)+"');");
        sendMsg(message,"Запись успешно добавлена");
    } catch (SQLException e) {
        sendMsg(message,"Ошибка добавления записи");
        e.printStackTrace();
    }

}
    public  void removeNote(Message message){
        int number = -1;
try {
     number= Integer.parseInt(message.getText().replace("/delete","").replace(" ",""));

}catch (NumberFormatException e){
    sendMsg(message,"Ошибка, ожидалось число");
}
        try {

            ResultSet resultSet = statement.executeQuery("SELECT * FROM notes WHERE USER_ID = "+message.getChatId()+";");
            int count = 1;
            while (true){
if(resultSet.next()){
    if(count == number) {
        System.out.println(count +" " +number);
        String execute="DELETE FROM  notes WHERE id ="+resultSet.getInt(1)+" AND USER_ID ="+message.getChatId()+";";
        statement.executeUpdate(execute);
        System.out.println(execute);
        sendMsg(message,"Запись успешно удалена");
        resultSet.close();
        break;
    }

}else {

    resultSet.close();
    break;
}
                count++;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void getNotesFromId(Message message){
        try {
            String result="";
            ResultSet resultSet = statement.executeQuery("SELECT * FROM notes WHERE USER_ID = "+message.getChatId()+";");
           int count = 1;
            while (resultSet.next()){

               result+= count+"."+ resultSet.getString(4)+"\n";
                count++;
            }
            sendMsg(message,result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getBotUsername() {
        return "n0tes_bot";
    }

    public String getBotToken() {
        return "942711950:AAEMGdAPfzH-1cblHaCUK5nKzNmfYfSlO_k";
    }
}
