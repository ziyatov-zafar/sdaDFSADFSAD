package uz.zafar.logisticsapplication.db.service.impl;

//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.zafar.logisticsapplication.db.domain.Channel;
import uz.zafar.logisticsapplication.db.repositories.ChannelRepository;
import uz.zafar.logisticsapplication.db.service.ChannelService;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.List;
import java.util.Optional;

@Service
//@Log4j2
public class ChannelServiceImpl implements ChannelService {
    @Autowired
    private  ChannelRepository channelRepository;


    @Override
    public ResponseDto<Channel> findById(Long id) {
        try {
            Optional<Channel> checkChannel = channelRepository.findById(id);
           /* return checkChannel.map(channel ->
                    new ResponseDto<>(true, "Ok", channel)).orElseGet(() ->
                    new ResponseDto<>(false, "Not found channel"));*/
            ResponseDto<Channel>res = new ResponseDto<>();
            if (checkChannel.isEmpty()){
                res.setData(null);
                res.setSuccess(false);
                res.setMessage("Not found channel");
            }else{
                res.setData(checkChannel.get());
                res.setSuccess(true);
                res.setMessage("Ok");
            }
            return res;
        } catch (Exception e) {

            return new ResponseDto<>(false , e.getMessage());
        }
    }

    @Override
    public ResponseDto<Channel> findByChannelId(long channelId) {
        try {
            Optional<Channel> checkChannel = channelRepository.getChannelById(channelId);
            return checkChannel.map(channel -> new ResponseDto<>(true, "Ok", channel)).orElseGet(() -> new ResponseDto<>(false, "Not found channel", null));
        } catch (Exception e) {
            return new ResponseDto<>(false , e.getMessage());
        }
    }

    @Override
    public ResponseDto<Void> save(Channel channel) {
        try {
            channelRepository.save(channel);
            return new ResponseDto<>(true, "Ok", null);
        } catch (Exception e) {
            return new ResponseDto<>(false , e.getMessage());
        }
    }

    @Override
    public ResponseDto<Channel> findByStats(String status) {
        try {
            Channel channel = channelRepository.findByStatus(status);
            if (channel != null) {
                return new ResponseDto<>(true, "Ok", channel);
            }return  new ResponseDto<>(false, "Not found channel", null);
        } catch (Exception e) {
            return new ResponseDto<>(false , e.getMessage());
        }
    }

    @Override
    public ResponseDto<List<Channel>> findAll() {
        try {
            return new ResponseDto<>(true , "Ok", channelRepository.findAllByActiveIsTrueAndStatusOrderById("open"));
        } catch (Exception e) {
            return new ResponseDto<>(false , e.getMessage());
        }
    }
}
