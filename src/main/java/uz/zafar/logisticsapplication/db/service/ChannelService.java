package uz.zafar.logisticsapplication.db.service;

import org.springframework.data.domain.Page;
import uz.zafar.logisticsapplication.db.domain.Channel;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.List;

public interface ChannelService {
    ResponseDto<Channel>findById(Long channelId);
    ResponseDto<Channel>findByChannelId(long channelId);
    ResponseDto<List<Channel>>findAll();
    ResponseDto<Channel>findByStats(String status);
    ResponseDto<Void>save(Channel channel);
}
